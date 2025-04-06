package com.codehive.service.impl;

import com.codehive.dto.TokenResponse;
import com.codehive.entity.Role;
import com.codehive.entity.User;
import com.codehive.repository.RoleRepository;
import com.codehive.repository.UserRepository;
import com.codehive.security.JwtTokenProvider;
import com.codehive.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GitHubUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.oauth2.github.redirect-uri}")
    private String redirectUri;

    public User processGitHubUser(Map<String, Object> userInfo) {
        String githubId = userInfo.get("id").toString();
        String login = (String) userInfo.get("login");
        String email = extractPrimaryEmail(userInfo);


        Optional<User> existingUser = userRepository.findByGithubId(githubId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        if (email != null && !email.endsWith("@gmail.com")) {
            existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                User user = existingUser.get();
                user.setGithubId(githubId);
                return userRepository.save(user);
            }
        }

        User newUser = new User();
        newUser.setGithubId(githubId);
        newUser.setUsername(login);
        newUser.setEmail(email);
        newUser.setFullName((String) userInfo.getOrDefault("name", login));
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setEmailVerified(true);
        roleRepository.findByName("USER").ifPresent(role ->
                newUser.getRoles().add(role)
        );
        return userRepository.save(newUser);
    }

    private String extractPrimaryEmail(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");

        // If email is private , make a separate API call to get emails
        if (email == null) {
            return attributes.get("login") + "@github.com";
        }
        return email;
    }

    public TokenResponse generateTokens(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        refreshTokenService.createRefreshToken(
                user.getUsername(),
                refreshToken,
                jwtTokenProvider.getJwtProperties().getRefreshTokenExpiration()
        );

        return new TokenResponse(accessToken, refreshToken);
    }
}
