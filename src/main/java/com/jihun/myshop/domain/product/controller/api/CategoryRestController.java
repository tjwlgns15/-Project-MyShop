package com.jihun.myshop.domain.product.controller.api;

import com.jihun.myshop.domain.product.entity.dto.CategoryDto;
import com.jihun.myshop.domain.product.entity.dto.CategoryDto.CategoryResponseDto;
import com.jihun.myshop.domain.product.service.CategoryService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jihun.myshop.domain.product.entity.dto.CategoryDto.CategoryCreateDto;
import static com.jihun.myshop.domain.product.entity.dto.CategoryDto.CategoryUpdateDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    @PostMapping("/new")
    public ApiResponseEntity<CategoryDto.CategoryResponseDto> createCategory(@RequestBody CategoryCreateDto request) {
        CategoryResponseDto response = categoryService.createCategory(request);
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/{categoryId}")
    public ApiResponseEntity<CategoryDto.CategoryResponseDto> getCategory(@PathVariable Long categoryId) {
        CategoryDto.CategoryResponseDto response = categoryService.getCategory(categoryId);
        return ApiResponseEntity.success(response);
    }

    @GetMapping
    public ApiResponseEntity<List<CategoryDto.CategoryResponseDto>> getAllCategories() {
        List<CategoryDto.CategoryResponseDto> responses = categoryService.getRootCategories();
        return ApiResponseEntity.success(responses);
    }

    @GetMapping("/root")
    public ApiResponseEntity<List<CategoryDto.CategoryResponseDto>> getRootCategories() {
        List<CategoryDto.CategoryResponseDto> response = categoryService.getRootCategories();
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/{parentId}/subcategories")
    public ApiResponseEntity<List<CategoryDto.CategoryResponseDto>> getSubcategories(@PathVariable Long parentId) {
        List<CategoryDto.CategoryResponseDto> response = categoryService.getSubcategories(parentId);
        return ApiResponseEntity.success(response);
    }

    @PutMapping("/{categoryId}")
    public ApiResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryUpdateDto request) {
        CategoryDto.CategoryResponseDto response = categoryService.updateCategory(categoryId, request);
        return ApiResponseEntity.success(response);
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponseEntity.success("카테고리 삭제가 완료되었습니다.");
    }

}
