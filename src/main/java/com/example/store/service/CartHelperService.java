package com.example.store.service;

import com.example.store.entity.Cart;
import com.example.store.entity.CartItem;
import com.example.store.entity.Product;
import com.example.store.entity.User;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.repository.CartItemRepository;
import com.example.store.repository.CartRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CartHelperService {
    UserRepository userRepository;
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;

    public User getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public Cart getCartByUser(User user){
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
    }

    public Product getProductById(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOTFOUND));
    }

    public CartItem getCartItem(Cart cart, Product product){
        return cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(()-> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));
    }
}
