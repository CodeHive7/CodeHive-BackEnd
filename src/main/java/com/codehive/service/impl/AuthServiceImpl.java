package com.codehive.service.impl;

import com.codehive.dto.LoginRequest;
import com.codehive.dto.RegisterRequest;
import com.codehive.dto.TokenResponse;
import com.codehive.entity.Role;
import com.codehive.entity.User;
import com.codehive.entity.verificationToken.VerificationToken;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import com.codehive.security.JwtTokenProvider;
import com.codehive.service.AuthService;
import com.codehive.service.RefreshTokenService;
import com.codehive.service.email.EmailService;
import com.codehive.service.verificationToken.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;


    @Override
    public String register(RegisterRequest registerRequest) {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
             throw new RuntimeException("Username already exists");
        }

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setEmailVerified(false);

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role is not found"));

        user.getRoles().add(userRole);
        User savedUser = userRepository.save(user);

        // Generate and send Verification email
        try {
            String token = verificationTokenService.generateToken(savedUser);
            emailService.sendVerificationEmail(savedUser, token);
        } catch (Exception e) {
            userRepository.delete(savedUser);
            log.error("Error sending verification email: {}", e.getMessage());
        }
        return "User registered successfully";
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public String authenticate(LoginRequest loginRequest) {
        String loginCredentials = determineLoginCredential(loginRequest);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginCredentials , loginRequest.getPassword())
        );
        return jwtTokenProvider.generateAccessToken(authentication.getName());
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        String loginCredential = determineLoginCredential(loginRequest);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginCredential,
                        loginRequest.getPassword()
                )
        );

        User user ;
        Optional<User> userOptional = userRepository.findByUsername(loginCredential);
        if(userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(loginCredential);
        }
        user = userOptional.orElseThrow(() ->
                new RuntimeException("User not found with provided credentials")
        );

        if(!user.isActive()) {
            throw new RuntimeException("User is blocked");
        }

        if(!user.isEmailVerified()) {
            throw new RuntimeException("Pleas verify your email before logging in");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(authentication.getName());
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication.getName());

        refreshTokenService.createRefreshToken(
                authentication.getName(),
                refreshToken,
                jwtTokenProvider.getJwtProperties().getRefreshTokenExpiration()
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    private String determineLoginCredential(LoginRequest loginRequest) {
        if (loginRequest.getUsername() != null && !loginRequest.getUsername().isEmpty()) {
            return loginRequest.getUsername();
        } else if (loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()) {
            return loginRequest.getEmail();
        } else {
            throw new RuntimeException("Username or email is required");
        }
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        Optional<VerificationToken> optToken = verificationTokenService.findByToken(token);

        if (optToken.isEmpty()) {
            System.out.println("❌ Token not found in database: " + token);
            throw new RuntimeException("Invalid verification token");
        }

        VerificationToken verificationToken = optToken.get();
        System.out.println("✅ Found token for user: " + verificationToken.getUser().getUsername());


        if(verificationToken.isExpired()) {
            verificationTokenService.deleteToken(verificationToken);
            throw new RuntimeException("Verification Token has expired");
        }

        User user = verificationToken.getUser();
        if(user.isEmailVerified()) {
            return;
        }
        user.setEmailVerified(true);
        userRepository.save(user);

        // Delete the used token
        try {
            verificationTokenService.deleteToken(verificationToken);
        } catch (Exception e) {
            log.error("Error deleting verification token: {}", e.getMessage());
        }
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.isEmailVerified()) {
            throw new RuntimeException("Email is already verified");
        }

        String token = verificationTokenService.generateToken(user);
        emailService.sendVerificationEmail(user, token);
    }

}
