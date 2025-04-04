package com.jihun.myshop.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    ORDERED("주문 완료"),
    SHIPPED("배송중"),
    DELIVERED("배송 완료"),
    CANCELLED("주문 취소")
    ;

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
