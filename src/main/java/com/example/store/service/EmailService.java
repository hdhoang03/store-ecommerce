package com.example.store.service;

import com.example.store.dto.request.EmailCreationRequest;
import com.example.store.dto.request.SendVerificationEmailRequest;
import com.example.store.entity.Order;
import com.example.store.entity.OrderItem;
import com.example.store.entity.User;
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

    public void sendOrderSuccessEmail(User user, Order order){
        String orderCode = order.getOrderCode();
        System.out.println("order code" + orderCode);
        StringBuilder content = new StringBuilder();
        content.append("Cảm ơn bạn đã đặt hàng tại STORE!\n\n");
        content.append("Đơn hàng #").append(orderCode).append(" của bạn gồm:\n");

        for (OrderItem item : order.getItems()){
            content.append("- ")
                    .append(item.getProduct().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append("\n");
        }

        content.append("\nTổng tiền: ").append(order.getTotalPrice()).append(" VND\n");
        content.append("Địa chỉ giao hàng: ").append(order.getShippingAddress()).append("\n\n");
        content.append("Chúng tôi sẽ liên hệ bạn sớm nhất.");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        System.out.println("UserEmail: " + user.getEmail());
        message.setSubject("Đơn đặt hàng thành công - Mã đơn: " + orderCode);
        message.setText(content.toString());
        message.setFrom(username);
        javaMailSender.send(message);
    }
}
