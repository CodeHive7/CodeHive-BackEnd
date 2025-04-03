package com.codehive.service.impl.email;

import com.codehive.entity.User;
import com.codehive.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;
    @Value("${app.base-url}")
    private String baseUrl;

    @Async
    @Override
    public void sendVerificationEmail(User user, String verificationUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("CodeHive - Verify your email");
        message.setText("Hello " + user.getFullName() + ",\n\n" +
                "Please verify your email address by clicking the link below:\n" +
                verificationUrl + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "Thanks,\nThe CodeHive Team");

        mailSender.send(message);
    }
}
