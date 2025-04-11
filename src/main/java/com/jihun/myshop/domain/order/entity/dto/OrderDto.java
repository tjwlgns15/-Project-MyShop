package com.jihun.myshop.domain.order.entity.dto;

import com.jihun.myshop.domain.order.entity.OrderStatus;
import com.jihun.myshop.domain.order.entity.dto.OrderItemDto.OrderItemCreate;
import com.jihun.myshop.domain.order.entity.dto.OrderItemDto.OrderItemResponse;
import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressCreate;
import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderCreate {
        private List<OrderItemCreate> items;
        private AddressCreate shippingAddress;
        private AddressCreate billingAddress;
        private boolean sameAsBillingAddress;
        private BigDecimal shippingFee;
        private BigDecimal discountAmount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponse {
        private Long id;
        private String orderNumber;
        private Long userId;
        private String username;
        private OrderStatus orderStatus;
        private String orderStatusDescription;
        private List<OrderItemResponse> orderItems;
        private BigDecimal totalAmount;
        private BigDecimal shippingFee;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private LocalDateTime createdAt;
        private LocalDateTime paidAt;
        private LocalDateTime shippedAt;
        private LocalDateTime deliveredAt;
        private String trackingNumber;
        private String cancelReason;
        private AddressResponse shippingAddress;
        private AddressResponse billingAddress;
    }

    // 주문 상태 업데이트 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatusUpdateDto {
        private OrderStatus orderStatus;
    }

    // 주문 취소 요청 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderCancelDto {
        private String reason;
    }

    // 배송 추적 번호 설정 DTO
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingNumberDto {
        private String trackingNumber;
    }
}
