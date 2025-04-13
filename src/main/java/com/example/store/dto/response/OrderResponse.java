package com.example.store.dto.response;

import com.example.store.entity.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    String id;
    List<OrderItemResponse> items;
    Double totalAmount;
    LocalDateTime orderDate;
    OrderStatus status;
    UserResponse user;
    AddressResponse address;
}
