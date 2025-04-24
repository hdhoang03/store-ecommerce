package com.example.store.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
//    String shippingAddress;
    Long addressId;
    List<OrderItemRequest> items;
    //Phương thức thanh toán nếu có
}
