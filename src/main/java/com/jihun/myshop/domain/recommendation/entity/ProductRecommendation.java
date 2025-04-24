package com.jihun.myshop.domain.recommendation.entity;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_recommendations")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecommendation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private double score;

    @Enumerated(EnumType.STRING)
    private RecommendationType recommendationType;

    public enum RecommendationType {
        COLLABORATIVE_FILTERING,
        CONTENT_BASED,
        HYBRID,
        POPULAR,
        RECENTLY_VIEWED
    }
}
