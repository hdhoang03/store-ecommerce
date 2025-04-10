package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.OrderRequest;
import com.example.store.dto.response.OrderResponse;
import com.example.store.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping
    ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest request){
        return ApiResponse.<OrderResponse>builder()
                .code(1007)
                .result(orderService.createOrderFromCart(request))
                .build();
    }
}
