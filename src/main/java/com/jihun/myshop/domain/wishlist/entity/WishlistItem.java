package com.jihun.myshop.domain.wishlist.entity;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wishlist_items")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public WishlistItem(User user, Product product) {
        this.user = user;
        this.product = product;
    }
}