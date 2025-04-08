package com.jihun.myshop.domain.order.entity;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("신용카드 결제"),
    DEBIT_CARD("체크카드 결제"),
    BANK_TRANSFER("계좌이체"),
    PAYPAL("페이팔 결제"),
    POINT("포인트 결제");

    ;

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

}
