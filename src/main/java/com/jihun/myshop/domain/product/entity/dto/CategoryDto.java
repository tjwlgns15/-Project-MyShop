package com.jihun.myshop.domain.product.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CategoryDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryCreateDto {
        private String name;
        private String description;
        private Long parentId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponseDto {
        private Long id;
        private String name;
        private String description;
        private Long parentId;
        private String parentName;
        private List<CategoryResponseDto> subcategories;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryUpdateDto {
        private String name;
        private String description;
        private Long parentId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryBrief {
        private Long id;
        private String name;
    }
}
