package com.example.store.service;

import com.example.store.constaint.OrderStatus;
import com.example.store.dto.request.OrderItemRequest;
import com.example.store.dto.request.OrderRequest;
import com.example.store.dto.response.OrderResponse;
import com.example.store.entity.*;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {
    OrderMapper orderMapper;
    OrderRepository orderRepository;
    UserRepository userRepository;
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrderFromCart(OrderRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        List<OrderItemRequest> orderItemRequests = orderMapper.toOrderItemRequestList(cartItems);

        OrderRequest orderRequest = OrderRequest.builder()
                .items(orderItemRequests)
                .shippingAddress(request.getShippingAddress())
                .build();

        Order order = orderMapper.toOrder(orderRequest);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);//moi them
        order.setDeleted(false);

        List<OrderItem> orderItems = orderItemRequests.stream().map(req -> {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

            //Kiểm tra số lượng trong kho
            if(product.getQuantity() < req.getQuantity()){
                throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH_STOCK);
            }

            //giảm số lượng và cập nhật lại
//            product.setQuantity(product.getQuantity() - req.getQuantity());
//            product.setSold(product.getSold() + req.getQuantity());
//            productRepository.save(product);

            return OrderItem.builder()
                    .product(product)
                    .price(product.getPrice())
                    .quantity(req.getQuantity())
                    .order(order)
                    .build();
        }).toList();

        order.setItems(orderItems);
        double totalPrice = calculateTotalPrice(orderItems);
        order.setTotalPrice(totalPrice);
        Order savedOrder = orderRepository.save(order);

        cartRepository.delete(cart);

        //Sau khi lưu đơn hàng với trạng thái Shopeed hoặc Delivered cập nhật số lượng kho và bán được (moi them)
        if(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHOPPED){
            orderItems.forEach(orderItem -> {
                Product product = orderItem.getProduct();
                product.setQuantity(product.getQuantity() - orderItem.getQuantity());
                product.setSold(product.getSold() + orderItem.getQuantity());
                productRepository.save(product);
            });
        }

        return orderMapper.toOrderResponse(savedOrder);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponse> getAllOrders(Pageable pageable){
        return orderRepository.findAll(pageable)
                .map(orderMapper::toOrderResponse);
    }

    public Page<OrderResponse> getUserOrders(Pageable pageable){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return orderRepository.findByUser(user, pageable)
                .map(orderMapper::toOrderResponse);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteOrder(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateOrderStatus(Long id, OrderStatus newStatus){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_EXISTED));

        if(Boolean.TRUE.equals(order.getDeleted())){
            throw new AppException(ErrorCode.ORDER_ALREADY_DELETED);
        }

        OrderStatus oldStatus = order.getStatus();

        //Nếu chuyển trạng thái chưa giao sang SHOPPED hoặc DELIVERED
        if((newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.SHOPPED)
                && (oldStatus != OrderStatus.DELIVERED && oldStatus != OrderStatus.SHOPPED)){
            for(OrderItem item : order.getItems()){
                Product product = item.getProduct();
                int orderedQuantity = item.getQuantity();

                if(product.getQuantity() < orderedQuantity){
                    throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH_STOCK);
                }
                product.setQuantity(product.getQuantity() - orderedQuantity);
                product.setSold(product.getSold() + orderedQuantity);
                productRepository.save(product);
            }
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    private double calculateTotalPrice(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()){
            return 0.0;
        }
        return orderItems.stream()
                .mapToDouble(item -> {
           if (item.getPrice() == null || item.getQuantity() == null){
               throw new IllegalStateException("Price or quantity can't be null for order item");
           }
           return item.getPrice() * item.getQuantity();
        })
                .sum();
    }
}
