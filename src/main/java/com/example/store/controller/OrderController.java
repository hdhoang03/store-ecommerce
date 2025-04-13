package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.OrderCreationRequest;
import com.example.store.dto.request.OrderStatusUpdateRequest;
import com.example.store.dto.response.OrderResponse;
import com.example.store.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @PostMapping
    public ApiResponse<OrderResponse> placeOrder(@RequestBody OrderCreationRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.placeOrder(request))
                .build();
    }

    @GetMapping("/me")
    public ApiResponse<?> getUserOrders() {
        return ApiResponse.builder()
                .result(orderService.getUserOrders())
                .build();
    }

    @GetMapping
    public ApiResponse<?> getAllOrders() {
        return ApiResponse.builder()
                .result(orderService.getAllOrders())
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse<?> getOrderDetail(@PathVariable String orderId) {
        return ApiResponse.builder()
                .result(orderService.getOrderDetail(orderId))
                .build();
    }

    @PutMapping("/update-status")
    public ApiResponse<OrderResponse> updateOrderStatus(@RequestBody OrderStatusUpdateRequest request) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(request))
                .build();
    }

}
