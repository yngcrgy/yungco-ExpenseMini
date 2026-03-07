package com.expensemini.backend.service;

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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to ExpenseMini!");
        message.setText("Hi " + firstName
                + ",\n\nWelcome to ExpenseMini! The easiest way to track your daily expenses.\n\nBest,\nThe ExpenseMini Team");
        emailSender.send(message);
    }

    public void sendSystemNotification(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[Notification] " + subject);
        message.setText(body);
        emailSender.send(message);
    }
}
