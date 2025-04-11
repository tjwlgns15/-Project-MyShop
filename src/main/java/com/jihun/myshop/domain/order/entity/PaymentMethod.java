package com.jihun.myshop.domain.order.entity;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("신용카드"),
    BANK_TRANSFER("계좌이체"),
    VIRTUAL_ACCOUNT("가상계좌"),
    MOBILE_PAYMENT("모바일결제"),
    KAKAO_PAY("카카오페이"),
    NAVER_PAY("네이버페이"),
    POINT("포인트결제")

    ;

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

}
