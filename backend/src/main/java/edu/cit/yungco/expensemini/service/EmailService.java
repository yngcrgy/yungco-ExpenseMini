package edu.cit.yungco.expensemini.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username:noreply@expensemini.com}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String firstName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to ExpenseMini!");
            message.setText("Hi " + firstName
                    + ",\n\nWelcome to ExpenseMini! The easiest way to track your daily expenses.\n\nBest,\nThe ExpenseMini Team");
            emailSender.send(message);
            System.out.println("Welcome email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("FAILED to send welcome email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendSystemNotification(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("[Notification] " + subject);
            message.setText(body);
            emailSender.send(message);
            System.out.println("System notification sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("FAILED to send system notification to " + toEmail + ": " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetUrl) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request");
            message.setText("To reset your password, click the link below:\n" + resetUrl
                    + "\n\nIf you did not request a password reset, please ignore this email.");
            emailSender.send(message);
            System.out.println("Password reset email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("FAILED to send password reset email to " + toEmail + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
