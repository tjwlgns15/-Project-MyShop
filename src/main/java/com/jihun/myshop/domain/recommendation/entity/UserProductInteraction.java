package com.jihun.myshop.domain.recommendation.entity;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
유저 - 상품 상호작용을 저장
 */
@Entity
@Table(name = "user_product_interactions")
@Getter
@NoArgsConstructor
public class UserProductInteraction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int viewCount = 0;
    private int cartCount = 0;
    private int purchaseCount = 0;
    private boolean addedToWishlist = false;
    private Integer reviewRating = null;
    private LocalDateTime lastViewedAt;

    public UserProductInteraction(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void incrementCartCount() {
        this.cartCount++;
    }

    public void incrementPurchaseCount(int quantity) {
        this.purchaseCount += quantity;
    }

    public void setAddedToWishlist(boolean addedToWishlist) {
        this.addedToWishlist = addedToWishlist;
    }

    public void setReviewRating(Integer rating) {
        this.reviewRating = rating;
    }

    public void setLastViewedAt(LocalDateTime lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    // 사용자의 해당 상품에 대한 관심도 점수 계산
    public double calculateInterestScore() {
        double score = 0;

        // 조회 횟수에 따른 점수 (최대 5점)
        score += Math.min(viewCount * 0.5, 5.0);

        // 장바구니 추가 횟수에 따른 점수 (최대 10점)
        score += Math.min(cartCount * 2.0, 10.0);

        // 구매 횟수에 따른 점수 (최대 20점)
        score += Math.min(purchaseCount * 5.0, 20.0);

        // 위시리스트 추가 여부에 따른 점수
        if (addedToWishlist) {
            score += 7.0;
        }

        // 리뷰 평점에 따른 점수 (최대 15점)
        if (reviewRating != null) {
            score += reviewRating * 3.0;
        }

        return score;
    }
}