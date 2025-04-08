package com.jihun.myshop.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PAYMENT_PENDING("결제 대기중"),
    PAID("결제 완료"),
    PREPARING("상품 준비중"),
    SHIPPED("배송중"),
    DELIVERED("배송 완료"),
    CANCELED("주문 취소"),
    REFUNDED("환불 완료"),
    ;

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
