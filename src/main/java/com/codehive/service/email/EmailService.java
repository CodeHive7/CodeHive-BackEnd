package com.codehive.service.email;

import com.codehive.entity.User;

public interface EmailService {
    void sendVerificationEmail(User user, String verificationUrl);
}
