package com.codehive.controller;

import com.codehive.dto.LoginRequest;
import com.codehive.dto.RegisterRequest;
import com.codehive.dto.TokenResponse;
import com.codehive.security.JwtTokenProvider;
import com.codehive.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        String accessToken = authService.authenticate(loginRequest);
        String refreshoken = jwtTokenProvider.generateRefreshToken(loginRequest.getUsername());
        return ResponseEntity.ok(new TokenResponse(accessToken, refreshoken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody String refreshToken) {
        if(jwtTokenProvider.validateToken(refreshToken, true)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken, true);
            String newAccessToken = jwtTokenProvider.generateAccessToken(username);
            return ResponseEntity.ok(new TokenResponse(newAccessToken, refreshToken));
        } else {
            return ResponseEntity.status(401).body(null);
        }
    }
}
