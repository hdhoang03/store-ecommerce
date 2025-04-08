package com.example.store.mapper;

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
//    //Tính tổng tiền
//    @AfterMapping
//    default void calculateTotalPrice(Cart cart, @MappingTarget CartResponse cartResponse){
//        double total = cart.getItems().stream()
//                .mapToDouble(item-> {
//                    if(item.getQuantity() == null || item.getProduct().getPrice() == null){
//                        throw new IllegalStateException("Price or quantity is null for product: " + item.getProduct().getId());
//                    }
//                    return item.getProduct().getPrice() * item.getQuantity();
//                })
//                .sum();
//        cartResponse.setTotalPrice(total);
//        System.out.println("Calculated total price: " + total);//Log to debug
//    }
}
