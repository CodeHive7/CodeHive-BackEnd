package com.codehive.service.impl;

import com.codehive.entity.RefreshToken;
import com.codehive.repository.RefreshTokenRepository;
import com.codehive.security.JwtTokenProvider;
import com.codehive.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public RefreshToken createRefreshToken(String username, String token, long expiryDurationMs) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setToken(token);
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(Instant.now().plusMillis(expiryDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Override
    public boolean isValid(RefreshToken refreshToken) {
        return !refreshToken.isRevoked()
                && refreshToken.getExpiryDate().isAfter(Instant.now());
    }

    @Override
    public RefreshToken rotateRefreshToken(RefreshToken oldToken, String newToken) {
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setUsername(oldToken.getUsername());
        newRefreshToken.setToken(newToken);
        newRefreshToken.setExpiryDate(oldToken.getExpiryDate());
        newRefreshToken.setRevoked(false);

        return refreshTokenRepository.save(newRefreshToken);
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public void updateRefreshToken(RefreshToken oldToken, String newToken) {
        oldToken.setToken(newToken);
        oldToken.setExpiryDate(Instant.now().plusMillis(jwtTokenProvider.getJwtProperties().getRefreshTokenExpiration()));
        oldToken.setRevoked(false);
        refreshTokenRepository.save(oldToken);
    }
}
