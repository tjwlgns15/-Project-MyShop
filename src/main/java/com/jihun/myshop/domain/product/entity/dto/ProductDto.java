package com.jihun.myshop.domain.product.entity.dto;

import com.jihun.myshop.domain.product.entity.DiscountType;

import com.jihun.myshop.domain.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductCreateDto {
        private String name;
        private String description;
        private Long categoryId;
        private BigDecimal price;
        private int stockQuantity;
        private DiscountType discountType;
        private BigDecimal discountValue;
        private String mainImageUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductUpdateDto {
        private String name;
        private String description;
        private Long categoryId;
        private BigDecimal price;
        private DiscountType discountType;
        private BigDecimal discountValue;
        private int stockQuantity;
        private String mainImageUrl;
        private ProductStatus productStatus;
    }

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
        private String mainImageUrl;
        private String categoryName;
        private Long categoryId;
        private String sellerName;
        private Long sellerId;
        private ProductStatus productStatus;
        private double averageRating;
        private int totalReviews;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    /*public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .discountType(product.getDiscountType())
                .discountValue(product.getDiscountValue())
                .stockQuantity(product.getStockQuantity())
                .mainImageUrl(product.getMainImageUrl())
                .imageUrls(product.getImages() != null ?
                        product.getImages().stream()
                                .map(ProductImage::getImageUrl)
                                .collect(Collectors.toList()) :
                        new ArrayList<>())
                .categoryName(product.getCategory() != null ?
                        product.getCategory().getName() : null)
                .categoryId(product.getCategory() != null ?
                        product.getCategory().getId() : null)
                .sellerName(product.getSeller() != null ?
                        product.getSeller().getName() : null)
                .sellerId(product.getSeller() != null ?
                        product.getSeller().getId() : null)
                .productStatus(product.getProductStatus())
                .averageRating(product.getAverageRating())
                .totalReviews(product.getTotalReviews())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }*/
}
