package com.jihun.myshop.domain.product.entity;

import com.jihun.myshop.domain.product.entity.dto.ProductDto;
import com.jihun.myshop.domain.product.entity.dto.ProductDto.ProductUpdate;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Builder
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

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Long discountValue;

    private int stockQuantity;

    @Column(length = 1000)
    private String mainImageUrl;

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


    public void updateBasicInfo(String name, String description, Long price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public void updateCategory(Category category) {
        this.category = category;
    }
    public void updateDiscount(DiscountType discountType, Long discountValue, Long calculatedDiscountPrice) {
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.discountPrice = calculatedDiscountPrice;
    }
    public void updateStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    public void updateMainImage(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }
    public void updateStatus(ProductStatus productStatus) {
        this.productStatus = productStatus;
    }

    public void markAsDeleted() {
        this.productStatus = ProductStatus.DELETED;
    }
}
