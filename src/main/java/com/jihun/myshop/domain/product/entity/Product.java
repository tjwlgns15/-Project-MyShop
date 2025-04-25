package com.jihun.myshop.domain.product.entity;

import com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto.ProductCreateWithImagesDto;
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    private int stockQuantity;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal discountPrice;

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

    public static Product createProduct(ProductCreateWithImagesDto dto, Category category, User seller, BigDecimal discountPrice) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .discountType(dto.getDiscountType())
                .discountValue(dto.getDiscountValue())
                .discountPrice(discountPrice)
                .stockQuantity(dto.getStockQuantity())
                .category(category)
                .seller(seller)
                .productStatus(ProductStatus.ACTIVE)
                .images(new ArrayList<>())
                .build();
    }

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

    // 이미지 관련 메서드
    public void addImage(ProductImage image) {
        this.images.add(image);
    }

    public void removeImage(ProductImage image) {
        this.images.remove(image);
    }

    // 메인 이미지 조회 메서드
    public ProductImage getMainImage() {
        return this.images.stream()
                .filter(ProductImage::isMainImage)
                .findFirst()
                .orElse(null);
    }

    // 메인 이미지 URL 조회 메서드 (기존 코드와의 호환성을 위해)
    public String getMainImageUrl() {
        ProductImage mainImage = getMainImage();
        return mainImage != null ? mainImage.getImageUrl() : null;
    }

    // 추가 이미지 목록 조회
    public List<ProductImage> getAdditionalImages() {
        return this.images.stream()
                .filter(image -> !image.isMainImage())
                .sorted((i1, i2) -> Integer.compare(i1.getSortOrder(), i2.getSortOrder()))
                .toList();
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
