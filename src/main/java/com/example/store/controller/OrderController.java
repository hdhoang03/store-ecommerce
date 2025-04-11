package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.OrderRequest;
import com.example.store.dto.request.OrderStatusRequest;
import com.example.store.dto.response.OrderResponse;
import com.example.store.service.OrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/admin/get-all-orders")
    ApiResponse<Page<OrderResponse>> getAllOrders(
            @PageableDefault(page = 0, size = 2, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orderService.getAllOrders(pageable))
                .build();
    }

    @GetMapping("/my-orders")
    ApiResponse<Page<OrderResponse>> getUserOrders(
            @PageableDefault(page = 0, size = 2, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orderService.getUserOrders(pageable))
                .build();
    }

    @DeleteMapping("/admin/delete/{orderId}")
    ApiResponse<Void> softDeleteOrder(@PathVariable Long orderId){
        orderService.softDeleteOrder(orderId);
        return ApiResponse.<Void>builder()
                .message("Order has been deleted!")
                .build();
    }

    @PutMapping("/admin/status/{orderId}")
    ApiResponse<OrderResponse> updateOderStatus(@PathVariable Long orderId,
                                                @RequestBody OrderStatusRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, request.getStatus()))
                .build();
    }
}
