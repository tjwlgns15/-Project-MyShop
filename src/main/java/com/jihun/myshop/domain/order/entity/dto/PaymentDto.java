package com.jihun.myshop.domain.order.entity.dto;

import com.jihun.myshop.domain.order.entity.PaymentMethod;
import com.jihun.myshop.domain.order.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCreateDto {
        private Long orderId;
        private PaymentMethod paymentMethod;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponseDto {
        private Long id;
        private Long orderId;
        private String orderNumber;
        private PaymentMethod paymentMethod;
        private String paymentMethodDescription;
        private PaymentStatus paymentStatus;
        private String paymentStatusDescription;
        private BigDecimal amount;
        private String merchantUid;
        private String impUid;
        private String failReason;
        private LocalDateTime createdAt;
    }

    // 결제 완료 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCompleteDto {
        private String merchantUid;
        private String impUid;

    }

    // 결제 실패/취소 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCancelDto {
        private String reason;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentVerifyDto {
        private Long paymentId;
        private Long orderId;
        private String merchantUid;
        private String impUid;
        private BigDecimal amount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebhookDto {
        private String imp_uid;
        private String merchant_uid;
        private String status;
    }

}
