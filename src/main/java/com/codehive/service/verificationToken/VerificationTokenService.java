package com.codehive.service.verificationToken;

import com.codehive.entity.User;
import com.codehive.entity.verificationToken.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {
    String generateToken(User user);
    Optional<VerificationToken> findByToken(String token);
    void deleteToken(VerificationToken token);
}
