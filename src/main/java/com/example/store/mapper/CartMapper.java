package com.example.store.mapper;

import com.example.store.dto.request.AddToCartRequest;
import com.example.store.dto.request.OrderItemRequest;
import com.example.store.dto.response.CartItemResponse;
import com.example.store.dto.response.CartResponse;
import com.example.store.entity.Cart;
import com.example.store.entity.CartItem;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "price")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    @Mapping(source = "items", target = "items")
    @Mapping(target = "totalPrice", ignore = true)
    CartResponse toCartResponse(Cart cart);

    @IterableMapping(elementTargetType = CartItemResponse.class)
    List<CartItemResponse> toCartItemResponseList(List<CartItem> items);
}
