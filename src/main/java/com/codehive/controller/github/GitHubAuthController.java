package com.codehive.controller.github;

import com.codehive.dto.TokenResponse;
import com.codehive.entity.User;
import com.codehive.exception.BadRequestException;
import com.codehive.service.impl.GitHubUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

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

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> getGitHubLoginUrl() {
        String authUrl = "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + frontendUrl + "/auth/github/callback" +
                "&scope=user:email,read:user";

        Map<String, String> response = new HashMap<>();
        response.put("url", authUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<TokenResponse> processGitHubCallback(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null) {
            throw new BadRequestException("No code provided");
        }

        try {

            String token = exchangeCodeForToken(code);
            System.out.println("Token received:" + token.substring(0, Math.min(10, token.length())) + "...");
            System.out.println("Token type: " + (token.startsWith("gho_") ? "GitHub OAuth token" : "Other token type"));

            // Fetch GitHub user info
            Map<String, Object> userInfo = fetchGitHubUserInfo(token);
            System.out.println("GitHub user info: " + userInfo);

            // Process user info
            User user = gitHubUserService.processGitHubUser(userInfo);
            System.out.println("Processed user: " + user.getUsername() + ", ID: " + user.getId());

            // Generate JWT tokens
            TokenResponse tokens = gitHubUserService.generateTokens(user);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("GitHub authentication failed: " + e.getMessage());
        }
    }

    private String exchangeCodeForToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("client_id", clientId);
            requestBody.put("client_secret", clientSecret);
            requestBody.put("code", code);
            requestBody.put("redirect_uri", frontendUrl + "/auth/github/callback");

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://github.com/login/oauth/access_token",
                    request,
                    Map.class
            );

            System.out.println("GitHub token response: " + response.getBody());

            if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
                throw new BadRequestException("Failed to obtain access token: " +
                        (response.getBody() != null ? response.getBody().toString() : "null response"));
            }

            String accessToken = (String) response.getBody().get("access_token");
            System.out.println("Access token extracted: " + accessToken.substring(0, 5) + "...");
            return accessToken;
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
            Map<String, Object> fallbackUserInfo = new HashMap<>();
            fallbackUserInfo.put("id", token.hashCode());
            fallbackUserInfo.put("login", "github_user");
            fallbackUserInfo.put("name", "GitHub User");
            return fallbackUserInfo;
        }
    }
}
