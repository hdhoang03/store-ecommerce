package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.AddToCartRequest;
import com.example.store.dto.response.CartResponse;
import com.example.store.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @GetMapping("/my-cart")
    ApiResponse<CartResponse> getCart(){
        return ApiResponse.<CartResponse>builder()
                .code(1000)
                .result(cartService.getCartByUsername())
                .build();
    }

    @PostMapping("/add")
    ApiResponse<CartResponse> addToCart(@RequestBody AddToCartRequest request){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addToCart(request))
                .message("Product added to cart successfully.")
                .build();
    }

    @DeleteMapping("/delete/{productId}")
    ApiResponse<CartResponse> deleteFromCart(@PathVariable Long productId){
        cartService.deleteFromCart(productId);
        return ApiResponse.<CartResponse>builder()
                .code(1000)
                .message("Product has been removed from cart successfully.")
                .build();
    }
}
