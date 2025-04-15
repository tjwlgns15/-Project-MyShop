package com.jihun.myshop.domain.cart.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class CartItemDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemCreateDto {
        private Long productId;
        private int quantity;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponseDto {
        private Long id;
        private Long productId;
        private String productName;
        private String mainImageUrl;
        private int quantity;
        private BigDecimal price;
        private BigDecimal totalPrice;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemUpdateDto {
        private Long cartItemId;
        private int quantity;
    }
}
