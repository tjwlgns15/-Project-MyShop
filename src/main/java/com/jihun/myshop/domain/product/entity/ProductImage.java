package com.jihun.myshop.domain.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_images")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(length = 1000)
    private String imageUrl;

    @Column(length = 255)
    private String originalFileName;  // 원본 파일명
    @Column(length = 100)
    private String storedFileName;    // 저장된 파일명

    private int sortOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductImageType imageType;

    @Getter
    public enum ProductImageType {
        MAIN("메인"),
        ADDITIONAL("본문");

        private final String value;

        ProductImageType(String value) {
            this.value = value;
        }
    }

    // 메인 이미지용
    public static ProductImage createMainImage(Product product, String imageUrl, String originalFileName, String storedFileName) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .sortOrder(0)  // 메인 이미지는 항상 정렬 순서가 0
                .imageType(ProductImageType.MAIN)
                .build();
    }

    // 추가 이미지용
    public static ProductImage createAdditionalImage(Product product, String imageUrl, String originalFileName, String storedFileName, int sortOrder) {
        return ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .sortOrder(sortOrder)
                .imageType(ProductImageType.ADDITIONAL)
                .build();
    }

    public void updateImageUrl(String imageUrl, String originalFileName, String storedFileName) {
        this.imageUrl = imageUrl;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
    }

    public void updateSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void updateImageType(ProductImageType imageType) {
        this.imageType = imageType;
    }

    public boolean isMainImage() {
        return this.imageType == ProductImageType.MAIN;
    }
}