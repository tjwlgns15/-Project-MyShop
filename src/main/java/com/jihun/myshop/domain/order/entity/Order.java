package com.jihun.myshop.domain.order.entity;

import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

//    @OneToMany(fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;

    private BigDecimal totalAmount;  // 할인 전 총 금액
    private BigDecimal discountAmount;  // 할인 금액
    private BigDecimal shippingFee;  // 배송비
    private BigDecimal finalAmount;  // 최종 금액

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String trackingNumber;
    private String cancelReason;

    public static Order createOrder(User user, Address shippingAddress, Address billingAddress, BigDecimal shippingFee) {
        return Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .orderItems(new ArrayList<>())
                .orderStatus(OrderStatus.PAYMENT_PENDING)
                .shippingFee(shippingFee)
                .totalAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .finalAmount(BigDecimal.ZERO)
                .build();
    }

    private static String generateOrderNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();


    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
        recalculateAmounts();
    }

    private void recalculateAmounts() {
        this.totalAmount = calculateTotalAmount();
        this.finalAmount = calculateFinalAmount();
    }

    private BigDecimal calculateTotalAmount() {
        return orderItems.stream()
                .map(OrderItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // 최종 가격 계산 (상품 가격 + 배송비 - 할인)
    private BigDecimal calculateFinalAmount() {
        return totalAmount
                .add(shippingFee != null ? shippingFee : BigDecimal.ZERO)
                .subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    public void applyDiscount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
        recalculateAmounts();
    }
}
