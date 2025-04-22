package com.jihun.myshop.domain.product.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ReviewDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewRequestDto {
        @NotNull(message = "평점은 필수입니다.")
        @Min(value = 1, message = "평점은 최소 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 최대 5점까지 가능합니다.")
        private Integer rating;

        @Size(max = 1000, message = "리뷰 내용은 1000자 이내로 작성해주세요.")
        private String comment;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewResponseDto {
        private Long id;
        private Long userId;
        private String username;
        private Long productId;
        private String productName;
        private int rating;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDto {
        private Long productId;
        private int totalReviews;
        private double averageRating;
    }

}
