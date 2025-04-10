package com.jihun.myshop.domain.product.controller.api;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.dto.CategoryDto;
import com.jihun.myshop.domain.product.entity.dto.CategoryDto.CategoryResponse;
import com.jihun.myshop.domain.product.service.CategoryService;
import com.jihun.myshop.domain.user.entity.dto.UserResponse;
import com.jihun.myshop.domain.user.service.UserService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.jihun.myshop.domain.product.entity.dto.CategoryDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    @PostMapping("/new")
    public ApiResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryCreate request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/{categoryId}")
    public ApiResponseEntity<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        CategoryResponse response = categoryService.getCategory(categoryId);
        return ApiResponseEntity.success(response);
    }

    @GetMapping
    public ApiResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> responses = categoryService.getRootCategories();
        return ApiResponseEntity.success(responses);
    }

    @GetMapping("/root")
    public ApiResponseEntity<List<CategoryResponse>> getRootCategories() {
        List<CategoryResponse> response = categoryService.getRootCategories();
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/{parentId}/subcategories")
    public ApiResponseEntity<List<CategoryResponse>> getSubcategories(@PathVariable Long parentId) {
        List<CategoryResponse> response = categoryService.getSubcategories(parentId);
        return ApiResponseEntity.success(response);
    }

    @PutMapping("/{categoryId}")
    public ApiResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryUpdate request) {
        CategoryResponse response = categoryService.updateCategory(categoryId, request);
        return ApiResponseEntity.success(response);
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponseEntity.success("카테고리 삭제가 완료되었습니다.");
    }

}
