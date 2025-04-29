package com.example.store.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long id;
    String name;
    String description;
    Double price;
    Long categoryId;
    Integer quantity;
    Integer sold;
    List<ProductImageResponse> imageUrls;
}
