package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.ProductCreationRequest;
import com.example.store.dto.request.ProductUpdateRequest;
import com.example.store.dto.response.ProductResponse;
import com.example.store.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {
    ProductService productService;

    @PostMapping
    ApiResponse<ProductResponse> createProduct(@RequestBody ProductCreationRequest request){//thêm validate sau
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request))
                .build();
    }

    @GetMapping("/by-name/{name}")
    ApiResponse<ProductResponse> getProductByName(@PathVariable String name){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProductByName(name))
                .build();
    }

    @GetMapping("/by-id/{id}")
    ApiResponse<ProductResponse> getProductById(@PathVariable Long id){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProductById(id))
                .build();
    }

    @GetMapping
    ApiResponse<Page<ProductResponse>> getAllProduct(
            @PageableDefault(page = 0, size = 2, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){//mặc định nếu không truyền tham số pathvariable
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productService.getAllProducts(pageable))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteProductById(@PathVariable Long id){
        productService.deleteProduct(id);
        return ApiResponse.<String>builder()
                .result("Product has been deleted.")
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest request){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(id, request))
                .build();
    }
}
