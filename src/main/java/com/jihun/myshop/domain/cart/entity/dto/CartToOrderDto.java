package com.jihun.myshop.domain.cart.entity.dto;

import com.jihun.myshop.domain.user.entity.dto.AddressDto;
import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressCreate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class CartToOrderDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartOrderDto {
        private AddressCreate shippingAddress;
        private AddressCreate billingAddress;
        private boolean sameAsBillingAddress;
        private BigDecimal shippingFee;
        private BigDecimal discountAmount;

    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartOrderSelectDto {
        private List<Long> cartItemIds;
        private AddressCreate shippingAddress;
        private AddressCreate billingAddress;
        private boolean sameAsBillingAddress;
        private BigDecimal shippingFee;
        private BigDecimal discountAmount;
    }
}
