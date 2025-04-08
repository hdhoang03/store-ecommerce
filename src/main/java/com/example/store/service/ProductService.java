package com.example.store.service;

import com.example.store.dto.request.ProductCreationRequest;
import com.example.store.dto.request.ProductUpdateRequest;
import com.example.store.dto.response.ProductResponse;
import com.example.store.entity.Product;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.mapper.ProductMapper;
import com.example.store.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;

    public ProductResponse getProductByName(String name){
        return productMapper.toProductResponse(productRepository.findByName(name)
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOTFOUND)));
    }

    public List<ProductResponse> getAllProducts(){
        return productRepository.findAll().stream()
                .map(productMapper::toProductResponse).toList();//product -> productMapper.toProductResponse(product)
    }

    public ProductResponse getProductById(Long id){
        return productMapper.toProductResponse(productRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOTFOUND)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(ProductCreationRequest request){
        Product product = productMapper.toProduct(request);
        try {
            product = productRepository.save(product);//lưu sản phẩm vào db
        }catch (DataIntegrityViolationException e){
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }
        return productMapper.toProductResponse(product);//trả về thông tin sản phẩm từ entity sang dto
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request){
        Product product = productRepository.findById(id).orElseThrow(()-> new RuntimeException("Product not found."));

        productMapper.updateProduct(product, request);

        return productMapper.toProductResponse(productRepository.save(product));
    }
}
