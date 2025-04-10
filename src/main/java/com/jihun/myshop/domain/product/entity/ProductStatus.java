package com.jihun.myshop.domain.product.entity;

import lombok.Getter;

@Getter
public enum ProductStatus {
//    PENDING("승인대기중"),
    ACTIVE("판매중"),
    SOLD_OUT("품절"),
    INACTIVE("판매중지"),
    DELETED("삭제됨"),

    ;
    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

}
