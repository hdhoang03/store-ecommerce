package com.example.store.mapper;

import com.example.store.dto.request.AddToCartRequest;
import com.example.store.dto.request.OrderItemRequest;
import com.example.store.dto.request.OrderRequest;
import com.example.store.dto.response.OrderItemResponse;
import com.example.store.dto.response.OrderResponse;
import com.example.store.entity.CartItem;
import com.example.store.entity.Order;
import com.example.store.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "user", ignore = true)
    Order toOrder(OrderRequest request);

    @Mapping(source = "orderCode", target = "orderCode")
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    OrderItem toOrderItem(OrderItemRequest request);
    List<OrderItem> toOrderItemList(List<OrderItemRequest> requests);
    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> items);

    @Mapping(source = "product.id", target = "productId")
    OrderItemRequest toOrderItemRequest(CartItem cartItem);

    List<OrderItemRequest> toOrderItemRequestList(List<CartItem> cartItems);

    AddToCartRequest toCartRequest(OrderItemRequest request);
    //Nếu cần map danh sách
    List<AddToCartRequest> toCartRequestList(List<OrderItemRequest> requests);
}
