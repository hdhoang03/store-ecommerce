package com.example.store.service;
import com.example.store.dto.request.AddToCartRequest;
import com.example.store.dto.response.CartResponse;
import com.example.store.entity.Cart;
import com.example.store.entity.CartItem;
import com.example.store.entity.Product;
import com.example.store.entity.User;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.mapper.CartMapper;
import com.example.store.repository.CartItemRepository;
import com.example.store.repository.CartRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    CartMapper cartMapper;
    @Transactional
    /*
    * Một số thao tác như addToCart hoặc deleteFromCart có thay đổi nhiều bảng (cart, cartItem)
    * nên được đặt trong transaction để tránh lỗi khi một phần lưu, một phần không.
    * */
    public CartResponse addToCart(AddToCartRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUser(user).orElseGet(()-> {
            Cart newCart = Cart.builder()
                    .user(user)
                    .items(new ArrayList<>())
                    .build();
            return cartRepository.save(newCart);
        });

        //Tìm sản phẩm
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        //Tìm hoặc tạo mới cart item
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (item == null) {
            item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
        } else {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        }
        cartItemRepository.save(item);

        //Làm mới cart từ database
        Cart updateCart = cartRepository.findByUser(user)
                .orElseThrow(()-> new AppException(ErrorCode.CART_NOT_FOUND));

        //Tính tổng tiền sản phẩm với số lượng vừa thêm
//        updateCart.setItems(cartItemRepository.findByCart(updateCart));
//        return buildCartResponseWithTotal(updateCart);
        return cartMapper.toCartResponse(updateCart);
    }

    public CartResponse deleteFromCart(Long productId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //Tìm người dùng
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        //Tìm giỏ hàng người dùng
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(()-> new AppException(ErrorCode.CART_NOT_FOUND));

        //Tìm sản phẩm từ productId
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOTFOUND));

        //Tìm sản phẩm trong giỏ hàng load lại CartItem trực tiếp từ repo
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_IN_CART));

        //Xóa sản phẩm khỏi giỏ hàng
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        //Làm mới và trả về cart
        Cart updateCart = cartRepository.findByUser(user)
                .orElseThrow(()-> new AppException(ErrorCode.CART_NOT_FOUND));

        //Cập nhật lại giỏ hàng
//        updateCart.setItems(cartItemRepository.findByCart(updateCart));
//        return buildCartResponseWithTotal(updateCart);

        return cartMapper.toCartResponse(updateCart);
    }

    public CartResponse getCartByUsername(){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(()-> new AppException(ErrorCode.CART_NOT_FOUND));

        CartResponse response = buildCartResponseWithTotal(cart);

        System.out.println("CartResponse totalPrice: " + response.getTotalPrice()); // Log để debug

        return response;
    }

    //Tính tổng tiền
    private CartResponse buildCartResponseWithTotal(Cart cart){
        CartResponse response = cartMapper.toCartResponse(cart);

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                .sum();

        response.setTotalPrice(total);
        return response;
    }
}
