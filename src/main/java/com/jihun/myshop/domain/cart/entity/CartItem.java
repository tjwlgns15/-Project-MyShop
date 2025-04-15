package com.jihun.myshop.domain.cart.entity;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal totalPrice;


    public void updateQuantity(int quantity) {
        this.quantity = quantity;
        recalculateTotalPrice();
    }

    public void incrementQuantity() {
        this.quantity++;
        recalculateTotalPrice();
    }

    public void decrementQuantity() {
        if (this.quantity > 1) {
            this.quantity--;
            recalculateTotalPrice();
        }
    }

    private void recalculateTotalPrice() {
        if (product.getDiscountPrice() != null) {
            this.price = product.getDiscountPrice();
        } else {
            this.price = product.getPrice();
        }
        this.totalPrice = this.price.multiply(BigDecimal.valueOf(quantity));
    }

}
