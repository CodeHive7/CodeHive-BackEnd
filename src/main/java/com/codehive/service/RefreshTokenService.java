package com.codehive.service;

import com.codehive.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String username , String token , long expiryDurationMs);
    Optional<RefreshToken> findByToken(String token);
    void revokeToken(String token);
    boolean isValid(RefreshToken refreshToken);
    RefreshToken rotateRefreshToken(RefreshToken oldToken, String newToken);
}
