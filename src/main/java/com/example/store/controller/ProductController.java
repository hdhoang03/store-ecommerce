package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.ProductCreationRequest;
import com.example.store.dto.request.ProductUpdateRequest;
import com.example.store.dto.response.ProductImageResponse;
import com.example.store.dto.response.ProductResponse;
import com.example.store.entity.Product;
import com.example.store.service.ProductImageService;
import com.example.store.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {
    ProductService productService;
    ProductImageService productImageService;

    @PostMapping("/admin/add")
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
    ApiResponse<List<ProductResponse>> getAllProduct(){//mặc định nếu không truyền tham số pathvariable
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getAllProducts())
                .build();
    }

//    @GetMapping
//    ApiResponse<Page<ProductResponse>> getAllProduct(
//            @PageableDefault(page = 0, size = 2, sort = "id", direction = Sort.Direction.ASC) Pageable pageable){//mặc định nếu không truyền tham số pathvariable
//        return ApiResponse.<Page<ProductResponse>>builder()
//                .result(productService.getAllProducts(pageable))
//                .build();
//    }

    @DeleteMapping("/admin/delete/{id}")
    ApiResponse<String> deleteProductById(@PathVariable Long id){
        productService.deleteProduct(id);
        return ApiResponse.<String>builder()
                .result("Product has been deleted.")
                .build();
    }

    @PutMapping("/admin/edit/{id}")
    ApiResponse<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest request){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(id, request))
                .build();
    }

    @PostMapping("/admin/uploadImg/{productId}/images")
    public ResponseEntity<?> uploadImages(@PathVariable Long productId, @RequestParam("files") List<MultipartFile> files){
        Product product = productService.getProductEntityById(productId);
        List<ProductImageResponse> uploadedImages = productImageService.uploadAndSaveImages(files, product);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .code(1000)
                        .message("Thêm ảnh thành công!")
                        .result(uploadedImages)
                        .build());
    }

    @GetMapping("/images/{productId}")
    ApiResponse<List<ProductImageResponse>> getImages(@PathVariable Long productId){
        List<ProductImageResponse> images = productImageService.getImagesByProduct(productId);

        images.forEach(image ->
                image.setImageUrl(image.getImageUrl()));

        return ApiResponse.<List<ProductImageResponse>>builder()
                .code(1000)
                .result(productImageService.getImagesByProduct(productId))
                .build();
    }

    @DeleteMapping("/admin/deleteImg/{imageId}")
    ResponseEntity<ApiResponse<?>> deleteImage(@PathVariable Long imageId){
        productImageService.deleteImage(imageId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                .code(1000)
                .message("Delete image successfully!")
                .build());
    }

    @PutMapping("/admin/editImg/{imageId}")
    ApiResponse<?> updateImage(@PathVariable Long imageId, @RequestParam("files") MultipartFile file){
        productImageService.updateImage(imageId, file);
        return ApiResponse.builder()
                .code(1000)
                .message("Update image successfully!")
                .build();
    }
}
