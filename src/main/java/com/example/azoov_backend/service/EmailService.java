package com.example.azoov_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOTPEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@azoov.com");
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP - Azoov Business OS");
            message.setText(buildOTPEmailBody(otp));

            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            // In development, we'll log the OTP
            log.warn("OTP for {}: {}", toEmail, otp);
        }
    }

    private String buildOTPEmailBody(String otp) {
        return String.format("""
                Hello,

                You have requested to reset your password for Azoov Business OS.

                Your One-Time Password (OTP) is: %s

                This OTP will expire in 10 minutes.

                If you did not request this password reset, please ignore this email.

                Best regards,
                Azoov Business OS Team
                """, otp);
    }

    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@azoov.com");
            message.setTo(toEmail);
            message.setSubject("Welcome to Azoov Business OS");
            message.setText(String.format("""
                    Hello %s,

                    Welcome to Azoov Business OS!

                    Your account has been created successfully.

                    Best regards,
                    Azoov Business OS Team
                    """, fullName));

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }
}
