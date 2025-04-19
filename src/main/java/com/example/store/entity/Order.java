package com.example.store.entity;

import com.example.store.constaint.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    OrderStatus status;
    String shippingAddress; //cái này chỉ khi truyền vào address bằng tay
    @ManyToOne
    User user;
    Double totalPrice;

    @Builder.Default
    @Column(nullable = false)
    Boolean deleted = false;

    @Column(unique = true)
    String orderCode; //Mới thêm

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItem> items = new ArrayList<>();
}
