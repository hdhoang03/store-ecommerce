package com.example.store.service;

import com.example.store.configuration.VnPayConfig;
import com.example.store.configuration.VnPayUtil;
import com.example.store.dto.response.PaymentResponse;
import com.example.store.entity.Order;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentService {
    VnPayConfig vnPayConfig;
    OrderRepository orderRepository;

    public PaymentResponse createVnPayPayment(HttpServletRequest request){
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        String orderCode = request.getParameter("orderCode"); //Mới tạo
        log.info("Order code: {}", orderCode);

        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        Double trueAmount = order.getTotalPrice() * 100L;

        if(amount != trueAmount){
            throw new IllegalArgumentException("Amount doesn't match order");
        }

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(orderCode);//Mới thêm orderCode
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));

        vnpParamsMap.put("vnp_TxnRef", orderCode);//Mới thêm

        if (bankCode != null && !bankCode.isEmpty()){
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }

        vnpParamsMap.put("vnp_IpAddr", VnPayUtil.getIpAddress(request));

        String queryUrl = VnPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VnPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VnPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;

        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return PaymentResponse.builder()
                .vnpResponseCode("00")
                .message("Success")
                .paymentUrl(paymentUrl)
                .build();
    }
}