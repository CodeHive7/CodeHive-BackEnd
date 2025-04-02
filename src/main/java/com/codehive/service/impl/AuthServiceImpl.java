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

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
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
            String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;
            emailService.sendVerificationEmail(savedUser, verificationUrl);
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        return jwtTokenProvider.generateAccessToken(authentication.getName());
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
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

    @Override
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if(verificationToken.isExpired()) {
            verificationTokenService.deleteToken(verificationToken);
            throw new RuntimeException("Verification Token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        // Delete the used token
        verificationTokenService.deleteToken(verificationToken);
    }

    @Override
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.isEmailVerified()) {
            throw new RuntimeException("Email is already verified");
        }

        String token = verificationTokenService.generateToken(user);
        String verificationUrl = baseUrl + "/api/auth/verify-email?token=" + token;
        emailService.sendVerificationEmail(user, verificationUrl);
    }

}
