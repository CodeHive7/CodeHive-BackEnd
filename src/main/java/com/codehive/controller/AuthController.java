package com.codehive.controller;

import com.codehive.dto.LoginRequest;
import com.codehive.dto.RegisterRequest;
import com.codehive.dto.TokenBody;
import com.codehive.dto.TokenResponse;
import com.codehive.security.JwtTokenProvider;
import com.codehive.service.AuthService;
import com.codehive.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokens = authService.login(loginRequest);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String ,String >> logout(@RequestBody TokenBody body) {
        refreshTokenService.revokeToken(body.refreshToken);
        Map<String ,String > response = new HashMap<>();
        response.put("message","Logout successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody String refreshToken) {
        var optional = refreshTokenService.findByToken(refreshToken);
        if(optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        var existingToken = optional.get();
        if(!refreshTokenService.isValid(existingToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        if(!jwtTokenProvider.validateToken(refreshToken,true)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken,true);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
        refreshTokenService.rotateRefreshToken(existingToken, newRefreshToken);

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);

        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
    }
}
