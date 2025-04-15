package com.jihun.myshop.domain.cart.entity.dto;

import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartResponseDto {
        private Long id;
        private List<CartItemResponseDto> items;
        private BigDecimal totalPrice;
        private int totalItems;
    }
}
