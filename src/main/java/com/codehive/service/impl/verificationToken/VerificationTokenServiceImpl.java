package com.codehive.service.impl.verificationToken;

import com.codehive.entity.User;
import com.codehive.entity.verificationToken.VerificationToken;
import com.codehive.repository.verificationToken.VerificationTokenRepository;
import com.codehive.service.verificationToken.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    @Value("${app.token.verification.expiration-hours:24}")
    private int tokenExpirationHours;


    @Override
    @Transactional
    public String generateToken(User user) {
        // Delete existing tokens for user
        tokenRepository.deleteByUserId(user.getId());

        // Create new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(tokenExpirationHours);

        VerificationToken verificationToken = new VerificationToken(token, user, expiryDate);
        tokenRepository.save(verificationToken);

        return token;
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteToken(VerificationToken token) {
        tokenRepository.delete(token);
    }
}
