package com.example.store.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    INVALID_QUANTITY(1001, "Invalid quantity", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED(403, "Access denied", HttpStatus.FORBIDDEN),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error.", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED(0001,"Product has been existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOTFOUND(0002, "Product not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1003, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTED(1004, "Username has been existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_IN_CART(1006, "Product not it cart", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1007, "Cart not found", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1008, "Category existed", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(1009, "Cart item not found", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXISTED(1010, "Order not existed", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_DELETED(1011, "Order already deleted", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_ENOUGH_STOCK(1012, "Product not enough stock", HttpStatus.NOT_FOUND),
    ACCOUNT_DISABLE(1013, "User account is disabled", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_FOUND(1014, "Address not found", HttpStatus.NOT_FOUND),

    ;

    int code;
    String message;
    HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode){
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
