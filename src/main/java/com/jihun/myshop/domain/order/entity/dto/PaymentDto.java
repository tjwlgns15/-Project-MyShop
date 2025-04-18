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
        private String paymentKey;
        private String failReason;
        private LocalDateTime createdAt;
    }

    // 결제 완료 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCompleteDto {
        private String paymentKey;
    }

    // 결제 실패/취소 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentCancelDto {
        private String reason;
    }

}
