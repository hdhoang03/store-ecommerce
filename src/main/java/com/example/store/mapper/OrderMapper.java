package com.example.store.mapper;

import com.example.store.dto.request.OrderCreationRequest;
import com.example.store.dto.request.OrderItemRequest;
import com.example.store.dto.response.OrderItemResponse;
import com.example.store.dto.response.OrderResponse;
import com.example.store.dto.response.ProductResponse;
import com.example.store.dto.response.UserResponse;
import com.example.store.entity.Order;
import com.example.store.entity.OrderItem;
import com.example.store.entity.Product;
import com.example.store.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // ======= ENTITY -> RESPONSE =======

    OrderResponse toOrderResponse(Order order);

}
