package com.jihun.myshop.domain.wishlist.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WishlistItemDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishlistRequestDto {
        private Long productId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishlistResponseDto {
        private Long id;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private BigDecimal price;
        private BigDecimal discountPrice;
        private LocalDateTime createdAt;
    }
}
