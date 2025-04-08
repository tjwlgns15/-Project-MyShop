package com.jihun.myshop.domain.product.entity;

import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;

    private Long price;

    private Long discountPrice;
    private boolean onDiscount;
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    private Long discountValue;

    private int stockQuantity;

    private String mainImageUrl;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews = new ArrayList<>();
    private int totalReviews = 0;
    private double averageRating = 0.0;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

//    @OneToMany(mappedBy = "product")
//    private List<CartItem> cartItems = new ArrayList<>();
//
//    @OneToMany(mappedBy = "product")
//    private List<OrderItem> orderItems = new ArrayList<>();
}
