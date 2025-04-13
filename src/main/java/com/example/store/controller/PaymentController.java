package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.response.PaymentResponse;
import com.example.store.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ApiResponse<PaymentResponse> pay(HttpServletRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .message("Success")
                .result(paymentService.createVnPayPayment(request))
                .build();
    }

    @GetMapping("/vn-pay-callback")
    public ApiResponse<PaymentResponse> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if ("00".equals(status)) {
            return ApiResponse.<PaymentResponse>builder()
                    .message("Success")
                    .result(PaymentResponse.builder()
                            .vnpResponseCode("00")
                            .message("Success")
                            .paymentUrl(null)
                            .build())
                    .build();
        } else {
            return ApiResponse.<PaymentResponse>builder()
                    .code(400)
                    .message("Failed")
                    .build();
        }
    }
}
