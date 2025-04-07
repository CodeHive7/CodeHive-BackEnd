package com.codehive.controller.github;

import com.codehive.dto.TokenResponse;
import com.codehive.entity.User;
import com.codehive.exception.BadRequestException;
import com.codehive.service.impl.GitHubUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/github")
@RequiredArgsConstructor
public class GitHubAuthController {

    private final OAuth2AuthorizedClientService clientService;
    private final GitHubUserService gitHubUserService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${app.oauth2.github.redirect-uri}")
    private String redirectUri;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> getGitHubLoginUrl() {
        // Generate a random state value. In production, you should store and later validate it.
        String state = generateRandomState();

        String authUrl = "https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=user:email,read:user"
                + "&state=" + state;

        return ResponseEntity.ok(Map.of("url", authUrl));
    }

    @GetMapping("/callback")
    public void processGitHubCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state,
            HttpServletResponse response) throws IOException {
        if (code == null || code.isEmpty()) {
            throw new BadRequestException("No code provided");
        }
        try {
            // Exchange the code for a GitHub token
            String githubToken = exchangeCodeForToken(code);
            Map<String, Object> userInfo = fetchGitHubUserInfo(githubToken);

            // Process the user info (create/update user)
            User user = gitHubUserService.processGitHubUser(userInfo);

            // Generate JWT tokens for your application
            TokenResponse tokens = gitHubUserService.generateTokens(user);

            // Set tokens in cookies (HTTP-only is more secure)
            Cookie accessTokenCookie = new Cookie("accessToken", tokens.getAccessToken());
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // Set to true when using HTTPS
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(18000); // e.g. 5 hours

            Cookie refreshTokenCookie = new Cookie("refreshToken", tokens.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(2592000); // e.g. 30 days

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // Redirect the user to the frontend userHome page
            response.sendRedirect("http://localhost:5173/userHome");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("GitHub authentication failed: " + e.getMessage());
        }
    }


    private String exchangeCodeForToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", "application/json");

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("code", code);
            requestBody.add("redirect_uri", redirectUri);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://github.com/login/oauth/access_token",
                    requestEntity,
                    Map.class
            );

            if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
                throw new BadRequestException("Failed to obtain access token: " +
                        (response.getBody() != null ? response.getBody().toString() : "null response"));
            }
            return (String) response.getBody().get("access_token");
        } catch (Exception e) {
            throw new BadRequestException("GitHub token exchange failed: " + e.getMessage());
        }
    }

    private Map<String, Object> fetchGitHubUserInfo(String token) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            System.out.println("Using authorization header: token " + token.substring(0, 5) + "...");
            headers.set("Authorization", "token " + token);
            headers.set("User-Agent", "CodeHive-App");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>("", headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            return response.getBody();
        } catch (Exception e) {
            System.out.println("GitHub API error : " + e.getMessage());
            // Fallback user info in case of API error
            Map<String, Object> fallbackUserInfo = new HashMap<>();
            fallbackUserInfo.put("id", token.hashCode());
            fallbackUserInfo.put("login", "github_user");
            fallbackUserInfo.put("name", "GitHub User");
            return fallbackUserInfo;
        }
    }

    private String generateRandomState() {
        return UUID.randomUUID().toString();
    }
}
