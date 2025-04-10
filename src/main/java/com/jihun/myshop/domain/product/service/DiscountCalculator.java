package com.jihun.myshop.domain.product.service;

import com.jihun.myshop.domain.product.entity.DiscountType;
import org.springframework.stereotype.Component;

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
    public Long calculateDiscountPrice(Long price, DiscountType discountType, Long discountValue) {
        if (price == null || discountValue == null || discountValue <= 0) {
            return price;
        }

        if (discountType == DiscountType.PERCENTAGE) {
            return price - (price * discountValue / 100);
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            return Math.max(0, price - discountValue);
        }

        return price;
    }
}