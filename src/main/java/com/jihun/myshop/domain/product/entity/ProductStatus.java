package com.jihun.myshop.domain.product.entity;

import lombok.Getter;

@Getter
public enum ProductStatus {
    AVAILABLE("판매중"),
    OUT_OF_STOCK("재고없음"),
    DISCONTINUED("판매중지"),
    PENDING("승인대기중"),
    DELETED("삭제됨");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

}
