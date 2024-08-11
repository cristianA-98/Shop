package com.cristian.shop.service;

import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendEmail(String to, String subject, String text) {

        // Create a new email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("resetPassword@gmail.com"); // Sender's email address
        message.setTo(to); // Recipient's email address
        message.setSubject(subject); // Email subject
        message.setText(text); // Email body
        // Send the email
        emailSender.send(message);
    }
}