package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.CategoryCreationRequest;
import com.example.store.dto.request.CategoryUpdateRequest;
import com.example.store.dto.response.CategoryResponse;
import com.example.store.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryCreationRequest request) {
        var result = categoryService.createCategory(request);
        return ApiResponse.<CategoryResponse>builder()
                .message("Create category successfully")
                .result(result)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable Long id) {
        var result = categoryService.getCategoryById(id);
        return ApiResponse.<CategoryResponse>builder()
                .message("Get category by ID successfully")
                .result(result)
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        var result = categoryService.getAllCategories();
        return ApiResponse.<List<CategoryResponse>>builder()
                .message("Get all categories successfully")
                .result(result)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateRequest request) {
        var result = categoryService.updateCategory(id, request);
        return ApiResponse.<CategoryResponse>builder()
                .message("Update category successfully")
                .result(result)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .message("Delete category successfully")
                .build();
    }
}
