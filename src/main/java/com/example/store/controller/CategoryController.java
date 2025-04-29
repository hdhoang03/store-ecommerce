package com.example.store.controller;

import com.example.store.dto.ApiResponse;
import com.example.store.dto.request.CategoryCreationRequest;
import com.example.store.dto.request.CategoryUpdateRequest;
import com.example.store.dto.response.CategoryResponse;
import com.example.store.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/categories")
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryController {
    CategoryService categoryService;

    @GetMapping
    ApiResponse<List<CategoryResponse>> getAllCategory(){
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getAllCategories())
                .build();
    }

    @PostMapping("/admin/add")
    ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryCreationRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .message("Category has been created.")
                .result(categoryService.createCategory(request))
                .build();
    }

    @DeleteMapping("/admin/delete/{id}")
    ApiResponse<String> deleteCategoryById(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ApiResponse.<String>builder()
                .message("Category has been deleted.")
                .build();
    }

    @PutMapping("/admin/edit/{id}")
    ApiResponse<CategoryResponse> editCategory(@PathVariable Long id, @RequestBody CategoryUpdateRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(id, request))
                .build();
    }
}
