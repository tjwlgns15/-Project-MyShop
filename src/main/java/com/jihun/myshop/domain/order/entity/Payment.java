package com.jihun.myshop.domain.order.entity;

import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.jihun.myshop.domain.order.entity.OrderStatus.*;


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

    private String impUid;  // PG사 고유 결제 번호
    private String merchantUid;  // 서버측 주문 번호

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
    }

    public void markAsFailed(String failReason) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.failReason = failReason;
    }

    public void markAsCanceled(String reason) {
        this.paymentStatus = PaymentStatus.CANCELED;
        this.failReason = reason;
    }

    public void updateFromWebhook(String impUid, PaymentStatus status, String reason) {
        this.impUid = impUid;
        this.paymentStatus = status;
        if (status == PaymentStatus.COMPLETED) {
            this.paidAt = LocalDateTime.now();
        } else if (status == PaymentStatus.FAILED || status == PaymentStatus.CANCELED) {
            this.failReason = reason;
        }
    }

    public void updateAmount(BigDecimal newAmount) {
        if (this.paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 결제는 금액을 변경할 수 없습니다.");
        }
        this.amount = newAmount;
    }
}