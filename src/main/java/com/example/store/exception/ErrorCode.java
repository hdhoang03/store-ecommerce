package com.example.store.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    INVALID_KEY(1001, "Invalid message key", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCESS_DENIED(403, "Access denied", HttpStatus.FORBIDDEN),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error.", HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED(0001,"Product has been existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOTFOUND(0002, "Product not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1003, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTED(1004, "Username has been existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_IN_CART(1006, "Product not it cart", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1007, "Cart not found", HttpStatus.NOT_FOUND),
    ADDRESS_NOT_FOUND(1008, "Address not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(1009, "Order not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(1010, "You do not have permission", HttpStatus.FORBIDDEN),
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
