package com.jihun.myshop.domain.order.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class OrderItemDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemCreate {
        private Long productId;
        private int quantity;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productImageUrl;
        private int quantity;
        private BigDecimal price;
        private BigDecimal discount;
        private BigDecimal finalPrice;
    }
}
