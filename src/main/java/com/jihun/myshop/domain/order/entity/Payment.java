package com.jihun.myshop.domain.order.entity;

import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "payments")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String merchantUid;
    private String impUid;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private BigDecimal amount;
    private LocalDateTime paidAt;
    private String failReason;

    public static Payment createPayment(Order order, PaymentMethod paymentMethod) {
        return Payment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(order.getFinalAmount())
                .build();
    }

    public void markAsCompleted(String merchantUid, String impUid) {
        this.merchantUid = merchantUid;
        this.impUid = impUid;
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paidAt = LocalDateTime.now();

        if (order != null) {
            this.order.updateOrderStatus(OrderStatus.PAID);
        }
    }

    public void markAsFailed(String failReason) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.failReason = failReason;
    }

    public void markAsCanceled(String reason) {
        this.paymentStatus = PaymentStatus.CANCELED;
        this.failReason = reason;

        if (order != null) {
            this.order.updateOrderStatus(OrderStatus.CANCELED);
        }
    }

    public void updateAmount(BigDecimal newAmount) {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 결제는 금액을 변경할 수 없습니다.");
        }
        this.amount = newAmount;
    }
}