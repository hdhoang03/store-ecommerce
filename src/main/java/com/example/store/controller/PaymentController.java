package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.response.PaymentResponse;
import com.example.store.service.OrderService;
import com.example.store.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;
    OrderService orderService;

    @GetMapping("/vn-pay")
    ApiResponse<PaymentResponse> pay(HttpServletRequest request){
        return ApiResponse.<PaymentResponse>builder()
                .message("Success")
                .result(paymentService.createVnPayPayment(request))
                .build();
    }

    @GetMapping("/vn-pay-callback")
    ApiResponse<PaymentResponse> payCallbackHandler(HttpServletRequest request){
        String status = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef"); //lấy mã đơn hàng từ VNPAY callback (mới thêm)
        if ("00".equals(status)){
            orderService.updateOrderStatusToDelivered(txnRef);

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