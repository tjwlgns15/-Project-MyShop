package com.jihun.myshop.domain.product.entity.dto;

import com.jihun.myshop.domain.product.entity.DiscountType;
import com.jihun.myshop.domain.product.entity.ProductImage.ProductImageType;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductWithImageDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCreateWithImagesDto {
        private String name;
        private String description;
        private Long categoryId;
        private BigDecimal price;
        private int stockQuantity;
        private DiscountType discountType;
        private BigDecimal discountValue;

        // 이미지 정렬 순서 목록 (인덱스 기반)
        @Builder.Default
        private List<Integer> additionalImagesSortOrder = new ArrayList<>();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductUpdateWithImagesDto {
        private String name;
        private String description;
        private Long categoryId;
        private BigDecimal price;
        private DiscountType discountType;
        private BigDecimal discountValue;
        private int stockQuantity;
        private ProductStatus productStatus;

        // 기존 메인 이미지 ID (없으면 새로 추가)
        private Long mainImageId;

        // 이미지 작업 관련 필드
        @Builder.Default
        private List<Integer> additionalImagesSortOrder = new ArrayList<>();  // 새 추가 이미지 정렬 순서

        @Builder.Default
        private List<ImageToUpdate> imagesToUpdate = new ArrayList<>();  // 업데이트할 기존 이미지 정보

        @Builder.Default
        private List<Long> imagesToDelete = new ArrayList<>();  // 삭제할 이미지 ID 목록
    }

    /**
     * 이미지 업데이트 정보를 담는 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageToUpdate {
        private Long id;
        private int sortOrder;
    }

    /**
     * 개별 이미지 생성 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageCreateRequest {
        private String imageUrl;
        private int sortOrder;
        private ProductImageType imageType;

        public ProductImageCreateRequest(String imageUrl) {
            this.imageUrl = imageUrl;
            this.sortOrder = 0;
            this.imageType = ProductImageType.ADDITIONAL;
        }
    }

    /**
     * 개별 이미지 업데이트 요청 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageUpdateRequest {
        private Long id;
        private String imageUrl;
        private int sortOrder;
        private ProductImageType imageType;

        public ProductImageCreateRequest toCreateRequest() {
            return new ProductImageCreateRequest(this.imageUrl);
        }
    }

    /**
     * 이미지 응답 DTO
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductImageResponseDto {
        private Long id;
        private Long productId;
        private String imageUrl;
        private String originalFileName;
        private String storedFileName;
        private int sortOrder;
        private ProductImageType imageType;
    }

    /**
     * 상품 응답 DTO (이미지 정보 포함)
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponseDto {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private DiscountType discountType;
        private BigDecimal discountValue;
        private int stockQuantity;
        private String categoryName;
        private Long categoryId;
        private String sellerName;
        private Long sellerId;
        private ProductStatus productStatus;
        private double averageRating;
        private int totalReviews;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private ProductImageResponseDto mainImage;

        @Builder.Default
        private List<ProductImageResponseDto> additionalImages = new ArrayList<>();
    }
}