package com.jihun.myshop.domain.product.entity;

import com.jihun.myshop.user.entity.User;
import com.jihun.myshop.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String productDescription;

    private Long productPrice;
    private int productQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User register;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    // 할인 관련 필드 추가
    private boolean onDiscount;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Long discountValue;

}
