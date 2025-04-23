package com.example.store.service;

import com.example.store.dto.request.EmailCreationRequest;
import com.example.store.dto.request.SendVerificationEmailRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailService {
    @Value("${spring.mail.username}")
    String username;

    final JavaMailSender javaMailSender;

    public void sendEmail(EmailCreationRequest request){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(request.getRecipient());
        message.setSubject(request.getSubject());
        message.setText(request.getContent());
        message.setFrom(username);

        javaMailSender.send(message);
    }

    public void sendVerificationCode(SendVerificationEmailRequest request){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(request.getRecipientEmail());
        message.setSubject("Email Verification Code");
        message.setText("Your verification code is: " + request.getVerificationCode());
        message.setFrom(username);

        javaMailSender.send(message);
    }
}
