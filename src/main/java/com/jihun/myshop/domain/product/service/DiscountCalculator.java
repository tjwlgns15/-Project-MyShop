package com.jihun.myshop.domain.product.service;

import com.jihun.myshop.domain.product.entity.DiscountType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DiscountCalculator {

    /**
     * 할인 가격을 계산
     *
     * @param price 원래 가격
     * @param discountType 할인 유형 (PERCENTAGE 또는 FIXED_AMOUNT)
     * @param discountValue 할인 값
     * @return 할인된 가격
     */
    public BigDecimal calculateDiscountPrice(BigDecimal price, DiscountType discountType, BigDecimal discountValue) {
        if (price == null || discountValue == null || discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }

        if (discountType == DiscountType.PERCENTAGE) {
            BigDecimal discount = price.multiply(discountValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return price.subtract(discount);
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            BigDecimal discounted = price.subtract(discountValue);
            return discounted.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discounted;
        }

        return price;
    }
}