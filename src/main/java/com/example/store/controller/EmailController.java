package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.EmailCreationRequest;
import com.example.store.dto.response.EmailResponse;
import com.example.store.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {
    EmailService emailService;

    @PostMapping("/send")
    ApiResponse<EmailResponse> sendEmail(@RequestBody EmailCreationRequest request){
        emailService.sendEmail(request);
        return ApiResponse.<EmailResponse>builder()
                .code(1000)
                .message("Email has been sent successfully!")
                .build();
    }
}
