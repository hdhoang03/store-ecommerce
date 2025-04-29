package com.example.store.service;

import com.example.store.dto.request.CategoryCreationRequest;
import com.example.store.dto.request.CategoryUpdateRequest;
import com.example.store.dto.response.CategoryResponse;
import com.example.store.entity.Category;
import com.example.store.exception.AppException;
import com.example.store.exception.ErrorCode;
import com.example.store.mapper.CategoryMapper;
import com.example.store.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryService {
    CategoryMapper categoryMapper;
    CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories(){
        log.info("In method get users");//ghi log
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.CATEGORY_EXISTED));

        categoryMapper.updateCategory(category, request);
        category = categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryCreationRequest request){
//        Category category = categoryMapper.toCategory(request);
//        try {
//            category = categoryRepository.save(category);
//        } catch (DataIntegrityViolationException e){
//            if (categoryRepository.existsByName(request.getName())) {
//                throw new AppException(ErrorCode.CATEGORY_EXISTED);
//            }
//            throw e;
//        }
//        return categoryMapper.toCategoryResponse(category);
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.toCategory(request);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }
}
