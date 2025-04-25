package com.jihun.myshop.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* 인증/계정(AUTH) 관련 에러 - A로 시작 */
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST.value(), "A001", "이미 사용중인 계정입니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "A002", "등록되지 않은 권한 정보입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "A003", "비밀번호가 일치하지 않습니다."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST.value(), "A004", "회원 정보를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN.value(), "A005", "접근 권한이 없습니다."),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED.value(), "A006", "비활성화된 계정입니다."),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED.value(), "A007", "계정이 잠겼습니다."),
    CREDENTIALS_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "A008", "자격 증명이 만료되었습니다."),
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED.value(), "A009", "인증에 실패하였습니다."),

    /* 상품(PRODUCT) 관련 에러 - P로 시작 */
    CATEGORY_NOT_EXIST(HttpStatus.NOT_FOUND.value(), "P001", "카테고리를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "P002", "상품 정보를 찾을 수 없습니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST.value(), "P003", "재고량이 부족합니다."),
    PRODUCT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST.value(), "P004", "현재는 구매할 수 없는 상품입니다."),

    /* 리뷰(REVIEW) 관련 에러 - R로 시작 */
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "R001", "리뷰를 찾을 수 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "R002", "이미 해당 상품에 대한 리뷰를 작성하셨습니다."),
    REVIEW_NOT_ALLOWED(HttpStatus.FORBIDDEN.value(), "R003", "해당 상품을 구매하지 않아 리뷰를 작성할 수 없습니다."),

    /* 이미지(IMAGE) 관련 에러 - I로 시작 */
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "I001", "이미지를 찾을 수 없습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "I002", "이미지 업로드에 실패했습니다."),
    INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST.value(), "I003", "유효하지 않은 이미지 형식입니다."),

    /* 파일(FILE) 업로드 관련 에러 - F로 시작 */
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "F001", "파일 업로드 중 오류가 발생했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "F002", "파일을 찾을 수 없습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "F003", "파일 삭제 중 오류가 발생했습니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST.value(), "F004", "파일 크기가 제한을 초과했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST.value(), "F005", "유효하지 않은 파일 형식입니다."),

    /* 주문/결제(ORDER) 관련 에러 - O로 시작 */
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "O001", "주문 정보를 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "O002", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_EXIST(HttpStatus.BAD_REQUEST.value(), "O003", "이미 결제가 생성된 주문입니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST.value(), "O004", "결제 금액이 일치하지 않습니다."),
    PAYMENT_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST.value(), "O005", "결제에 실패하였습니다."),

    /* 장바구니(CART) 관련 에러 - C로 시작 */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "C001", "수량은 1 이상이어야 합니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "C002", "카트에서 물품을 찾지 못했습니다."),
    CART_IS_EMPTY(HttpStatus.BAD_REQUEST.value(), "C003", "카트가 비어있습니다."),
    DUPLICATE_CART_ITEM(HttpStatus.BAD_REQUEST.value(), "C004", "중복 선택된 품목이 있습니다."),
    CART_ITEMS_NOT_SELECTED(HttpStatus.BAD_REQUEST.value(), "C005", "선택된 장바구니 아이템이 없습니다."),

    /* 위시리스트(WISHLIST) 관련 에러 - W로 시작 */
    ALREADY_EXIST_ITEM(HttpStatus.BAD_REQUEST.value(), "W001", "이미 위시리스트에 추가된 상품입니다."),
    NOT_FOUND_ITEM(HttpStatus.NOT_FOUND.value(), "W002", "해당 상품이 위시리스트에 없습니다."),

    /* 기타(COMMON) 에러 - X로 시작 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "X001", "잘못된 요청입니다.");


    private final int status;
    private final String code;
    private final String message;
}