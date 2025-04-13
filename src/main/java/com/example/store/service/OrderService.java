package com.example.store.service;

import com.example.store.dto.request.OrderCreationRequest;
import com.example.store.dto.request.OrderStatusUpdateRequest;
import com.example.store.dto.response.OrderResponse;
import com.example.store.entity.*;
import com.example.store.entity.enums.OrderStatus;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.CartRepository;
import com.example.store.repository.OrderRepository;
import com.example.store.repository.ProductRepository;
import com.example.store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class OrderService {

    OrderRepository orderRepository;
    UserRepository userRepository;
    CartRepository cartRepository;
    OrderMapper orderMapper;

    @Transactional
    public OrderResponse placeOrder(OrderCreationRequest request) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Username từ token: {}", userName);

        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tải cart từ CartRepository với EntityGraph để tránh lỗi lazy
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_NOT_FOUND);
        }

        Address address = user.getAddresses().stream()
                .filter(addr -> addr.getId().equals(request.getAddressId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();
            double itemTotal = product.getPrice() * quantity;
            totalAmount += itemTotal;

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .user(user)
                .address(address)
                .items(new ArrayList<>())
                .totalAmount(totalAmount)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItem item : orderItems) {
            item.setOrder(order);
            order.getItems().add(item);
        }

        Order savedOrder = orderRepository.save(order);

        // Clear giỏ hàng và cập nhật lại
        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toOrderResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Lấy danh sách đơn hàng của user: {}", username);

        List<Order> orders = orderRepository.findByUserUsername(username);

        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    @PostAuthorize("hasRole('ADMIN') or returnObject.user.username == authentication.name")
    public OrderResponse getOrderDetail(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        return orderMapper.toOrderResponse(order);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateOrderStatus(OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setStatus(request.getStatus());
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toOrderResponse(updatedOrder);
    }

}
