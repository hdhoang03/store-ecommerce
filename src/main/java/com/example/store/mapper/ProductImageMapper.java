package com.example.store.mapper;

import com.example.store.dto.response.ProductImageResponse;
import com.example.store.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    @Mapping(source = "product.id", target = "productId")
    ProductImageResponse toProductImageResponse(ProductImage productImage);
    ProductImage toProductImage(ProductImageResponse request);
}
