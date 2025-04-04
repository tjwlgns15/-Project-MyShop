package com.jihun.myshop.domain.product.entity;

import lombok.Getter;

@Getter
public enum DiscountType {
    PERCENTAGE("퍼센트 할인"),
    FIXED_AMOUNT("정액 할인"),
    NONE("할인 없음");

    private final String description;

    DiscountType(String description) {
        this.description = description;
    }

}
