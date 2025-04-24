package com.jihun.myshop.domain.recommendation.entity.dto;

import com.jihun.myshop.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String mainImageUrl;
    private Long categoryId;
    private String categoryName;
    private int totalReviews;
    private double averageRating;
}
