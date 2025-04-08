package com.jihun.myshop.domain.order.entity;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("결제 진행 중"),
    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    REFUNDED("환불"),

    ;

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
