package com.example.store.service;

import com.example.store.configuration.VnPayUtil;
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

import java.time.LocalDateTime;
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
    AddressRepository addressRepository;
    EmailService emailService;

    @Transactional
    public OrderResponse createOrderFromCart(OrderRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        List<OrderItemRequest> orderItemRequests = orderMapper.toOrderItemRequestList(cartItems);

        Address shippingAddress = addressRepository.findById(request.getAddressId())
                .filter(address -> address.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

//        OrderRequest orderRequest = OrderRequest.builder()
//                .items(orderItemRequests)
//                .shippingAddress(request.getShippingAddress())
//                .build();

//        Order order = orderMapper.toOrder(
//                OrderRequest.builder()
//                        .items(orderItemRequests)
//                        .addressId()
//                        .build());

        //Tạo 1 order mới
        String orderCode = VnPayUtil.getRandomNumber(8);
        Order order = Order.builder()
                .orderCode(orderCode)
                .createdAt(LocalDateTime.now())
                .user(user)
                .shippingAddress(shippingAddress.getAddress())
                .status(OrderStatus.PENDING)
                .deleted(false)
                .build();

        List<OrderItem> orderItems = orderItemRequests.stream().map(req -> {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOTFOUND));

            //Kiểm tra số lượng trong kho
            if(product.getQuantity() < req.getQuantity()){
                throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH_STOCK);
            }

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
        if(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED){
            updateStock(order);
//            orderItems.forEach(orderItem -> {
//                Product product = orderItem.getProduct();
//                product.setQuantity(product.getQuantity() - orderItem.getQuantity());
//                product.setSold(product.getSold() + orderItem.getQuantity());
//                productRepository.save(product);
//            });
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
        if((newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.SHIPPED)
                && (oldStatus != OrderStatus.DELIVERED && oldStatus != OrderStatus.SHIPPED)){
            updateStock(order);
//            for(OrderItem item : order.getItems()){
//                Product product = item.getProduct();
//                int orderedQuantity = item.getQuantity();
//
//                if(product.getQuantity() < orderedQuantity){
//                    throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH_STOCK);
//                }
//                product.setQuantity(product.getQuantity() - orderedQuantity);
//                product.setSold(product.getSold() + orderedQuantity);
//                productRepository.save(product);
//            }
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toOrderResponse(updatedOrder);
    }

    //Mới mở hàm này
    public void updateOrderStatusToDelivered(String orderCode){
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(()-> new AppException(ErrorCode.ORDER_NOT_EXISTED)); //Mới thêm

        System.out.println("Order tìm được: " + order.getOrderCode());
        System.out.println("Trạng thái hiện tại: " + order.getStatus());

        if(order != null && order.getStatus() == OrderStatus.PENDING){
            updateStock(order);//Cập nhật số lượng sản phẩm trong kho
            order.setStatus(OrderStatus.DELIVERED);
            orderRepository.save(order);
            System.out.println("Chuẩn bị gửi email...");
            emailService.sendOrderSuccessEmail(order.getUser(), order);
            System.out.println("Đã gọi hàm gửi email.");
        } else {
            throw new RuntimeException("Order not found or already processed.");
        }
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

    public void updateStock(Order order){
        for(OrderItem item : order.getItems()){
            Product product = item.getProduct();
            int orderedQuantiy = item.getQuantity();

            if(product.getQuantity() < orderedQuantiy){
                throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH_STOCK);
            }

            product.setQuantity(product.getQuantity() - orderedQuantiy);
            product.setSold(product.getSold() + orderedQuantiy);
            productRepository.save(product);
        }
    }
}
