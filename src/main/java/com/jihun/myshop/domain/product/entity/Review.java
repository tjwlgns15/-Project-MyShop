package com.jihun.myshop.domain.product.entity;

import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.jihun.myshop.domain.product.entity.dto.ReviewDto.*;

@Entity
@Table(name = "reviews")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int rating;

    @Column(length = 1000)
    private String comment;


    public Review(User user, Product product, ReviewRequestDto reviewRequestDto) {
        this.user = user;
        this.product = product;
        this.rating = reviewRequestDto.getRating();
        this.comment = reviewRequestDto.getComment() != null ? reviewRequestDto.getComment() : "";

        product.addReview(this);
    }

    public void updateReview(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;

        this.product.updateAverageRating();
    }
}