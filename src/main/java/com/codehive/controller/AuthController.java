package com.codehive.controller;

import com.codehive.dto.LoginRequest;
import com.codehive.dto.RegisterRequest;
import com.codehive.dto.TokenBody;
import com.codehive.dto.TokenResponse;
import com.codehive.exception.BadRequestException;
import com.codehive.security.JwtTokenProvider;
import com.codehive.service.AuthService;
import com.codehive.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok("Email verified successfully. You can now login");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        try {
            authService.resendVerificationEmail(email);
            return ResponseEntity.ok("Verification email has been resent");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String ,String >> logout(@RequestBody TokenBody body) {
        refreshTokenService.revokeToken(body.refreshToken);
        Map<String ,String > response = new HashMap<>();
        response.put("message","Logout successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody Map<String,String> request) {
        String refreshToken = request.get("refreshToken");

        // This will explicitly throw NullPointerException when refreshToken is null
        if (refreshToken == null) {
            throw new BadRequestException("Refresh token is required");
        }

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
        refreshTokenService.updateRefreshToken(existingToken, newRefreshToken);

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);

        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
    }
}
