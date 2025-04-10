package com.example.store.dto.response;

import com.example.store.constaint.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    Long id;
    LocalDateTime createdAt;
    OrderStatus status;
    String shippingAddress;
    Double totalPrice;
    List<OrderItemResponse> items;
}
