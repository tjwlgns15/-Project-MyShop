package com.jihun.myshop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // auth
    DUPLICATE_USERNAME(400, "이미 사용중인계정입니다."),
    ROLE_NOT_FOUND(404, "등록되지 않은 권한 정보입니다."),
    INVALID_CREDENTIALS(401, "비밀번호가 일치하지 않습니다."),
    USER_NOT_EXIST(400, "회원 정보를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(403, "접근 권한이 없습니다."),
    ACCOUNT_DISABLED(401, "비활성화된 계정입니다."),
    ACCOUNT_LOCKED(401, "계정이 잠겼습니다."),
    CREDENTIALS_EXPIRED(401, "자격 증명이 만료되었습니다"),
    AUTHENTICATION_FAILED(401, "인증에 실패하였습니다."),

    // product
    CATEGORY_NOT_EXIST(404, "카테고리를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "상품 정보를 찾을 수 없습니다."),
    OUT_OF_STOCK(400, "재고량이 부족합니다."),
    PRODUCT_NOT_AVAILABLE(400, "현재는 구매할 수 없는 상품입니다."),


    // order
    ORDER_NOT_FOUND(404, "주문 정보를 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND(404, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_EXIST(400, "이미 결제가 생성된 주문입니다."),
    PAYMENT_AMOUNT_MISMATCH(404, "결제 금액이 일치하지 않습니다."),
    PAYMENT_VERIFICATION_FAILED(400, "결제에 실패하였습니다."),

    // cart
    INVALID_INPUT_VALUE(400, "수량은 1 이상이어야 합니다."),
    CART_ITEM_NOT_FOUND(404, "카트에서 물품을 찾지 못 했습니다."),
    CART_IS_EMPTY(400, "카트가 비어있습니다."),
    DUPLICATE_CART_ITEM(400, "중복 선택 된 품목이 있습니다."),
    CART_ITEMS_NOT_SELECTED(400, "선택된 장바구니 아이템이 없습니다"),




    // etc..
    BED_REQUEST(400, "잘못된 설정입니다.")


    ;
    private final int code;
    private final String message;
}

