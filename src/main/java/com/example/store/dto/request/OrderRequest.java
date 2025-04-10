package com.example.store.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String shippingAddress;
    List<OrderItemRequest> items;
    //Phương thức thanh toán nếu có
}
