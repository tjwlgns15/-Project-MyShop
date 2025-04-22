package com.jihun.myshop.domain.product.entity;

import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    private BigDecimal price;

    private BigDecimal discountPrice;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private BigDecimal discountValue;

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


    public void updateBasicInfo(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
    public void updateCategory(Category category) {
        this.category = category;
    }
    public void updateDiscount(DiscountType discountType, BigDecimal discountValue, BigDecimal calculatedDiscountPrice) {
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

    // 리뷰 관련 메서드
    public void addReview(Review review) {
        this.reviews.add(review);
        updateAverageRating();
    }
    public void removeReview(Review review) {
        this.reviews.remove(review);
        updateAverageRating();
    }
    public void updateAverageRating() {
        this.totalReviews = this.reviews.size();
        if (this.totalReviews > 0) {
            double sum = this.reviews.stream()
                    .mapToInt(Review::getRating)
                    .sum();
            this.averageRating = sum / this.totalReviews;
        } else {
            this.averageRating = 0.0;
        }
    }
}
