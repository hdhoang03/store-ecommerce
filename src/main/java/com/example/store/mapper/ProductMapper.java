package com.example.store.mapper;

import com.example.store.dto.request.ProductCreationRequest;
import com.example.store.dto.request.ProductUpdateRequest;
import com.example.store.dto.response.ProductResponse;
import com.example.store.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "sold", ignore = true)
    Product toProduct(ProductCreationRequest request);

    @Mapping(source = "category.id", target = "categoryId")
    ProductResponse toProductResponse(Product product);

    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);
}