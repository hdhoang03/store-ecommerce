package com.example.store.service;

import com.example.store.dto.response.ProductImageResponse;
import com.example.store.entity.Product;
import com.example.store.entity.ProductImage;
import com.example.store.mapper.ProductImageMapper;
import com.example.store.repository.ProductImageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProductImageService {
    ProductImageRepository productImageRepository;
    ImageStorageService imageStorageService;
    ProductImageMapper productImageMapper;
    ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductImageResponse> uploadAndSaveImages(List<MultipartFile> files, Product product){
        for (MultipartFile file : files){
            String filename = imageStorageService.saveImage(file);
            ProductImage image = ProductImage.builder()
                    .product(product)
                    .imageUrl(filename)
                    .build();

            product.getImageUrls().add(image);
            productImageRepository.save(image);
        }
        //chuyển danh sách ảnh từ entity sang response dto
        return product.getImageUrls().stream()
                .map(productImageMapper::toProductImageResponse)
                .collect(Collectors.toList());
    }

    public List<ProductImageResponse> getImagesByProduct(Long productId){
        List<ProductImage> images = productImageRepository.findByProduct_Id(productId);
        return images.stream()
//                .map(productImageMapper::toProductImageResponse)
                .map(img -> {
                    ProductImageResponse dto = productImageMapper.toProductImageResponse(img);
                    dto.setImageUrl("http://localhost:8080/store/uploads/images/" + dto.getImageUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteImage(Long imageId){
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Not found image with id: " + imageId));

        imageStorageService.deleteImage(productImage.getImageUrl());
        productImageRepository.delete(productImage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateImage(Long imageId, MultipartFile file){
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found!"));

        imageStorageService.deleteImage(productImage.getImageUrl());

        String newImageUrl = imageStorageService.saveImage(file);
        productImage.setImageUrl(newImageUrl);

        productImageRepository.save(productImage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateImages(Long productId, List<MultipartFile> files){
        List<ProductImage> oldImages = productImageRepository.findByProduct_Id(productId);
        for (ProductImage old : oldImages){
            imageStorageService.deleteImage(old.getImageUrl());
            productImageRepository.delete(old);
        }

        Product product = productService.getProductEntityById(productId);
        uploadAndSaveImages(files, product);
    }

    public ProductImageResponse getFirstImageByProductId(Long productId){
        List<ProductImage> images = productImageRepository.findByProduct_Id(productId);
        if(images.isEmpty()){
            throw new RuntimeException("Not found image.");
        }
        return productImageMapper.toProductImageResponse(images.get(0));
    }
}
