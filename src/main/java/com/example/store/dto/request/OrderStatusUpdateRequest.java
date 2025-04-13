package com.example.store.dto.request;

import com.example.store.entity.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusUpdateRequest {
    String orderId;
    OrderStatus status;
}
