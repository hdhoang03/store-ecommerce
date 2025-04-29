package com.example.store.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products")
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//tự tăng id
    Long id;
    String name;
    String description;
    Double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    Integer quantity;

    @Column(nullable = false)
    Integer sold = 0;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> imageUrls;
}
