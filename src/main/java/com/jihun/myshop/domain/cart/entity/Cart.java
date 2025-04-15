package com.jihun.myshop.domain.cart.entity;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.global.common.BaseTimeEntity;
import com.jihun.myshop.global.exception.CustomException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jihun.myshop.global.exception.ErrorCode.CART_ITEM_NOT_FOUND;

@Entity
@Table(name = "carts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalPrice;


    public void updateQuantity(Long cartItemId, int quantity) {
        CartItem cartItem = findCartItemById(cartItemId)
                .orElseThrow(() -> new CustomException(CART_ITEM_NOT_FOUND));

        cartItem.updateQuantity(quantity);
        recalculateTotalPrice();
    }

    public void addItem(Product product, int quantity) {

        if (this.items == null) {
            this.items = new ArrayList<>();
        }

        Optional<CartItem> existingItem = findCartItemByProduct(product);

        if (existingItem.isPresent()) {
            existingItem.get().updateQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            // 새 상품 추가
            BigDecimal itemPrice = product.getDiscountPrice() != null ? product.getDiscountPrice() : product.getPrice();
            BigDecimal itemTotalPrice = itemPrice.multiply(BigDecimal.valueOf(quantity));

            CartItem cartItem = CartItem.builder()
                    .cart(this)
                    .product(product)
                    .quantity(quantity)
                    .price(itemPrice)
                    .totalPrice(itemTotalPrice)
                    .build();
            this.items.add(cartItem);
        }
        recalculateTotalPrice();
    }

    public void removeItem(Long cartItemId) {
        boolean removed = false;

        if (this.items != null) {
            int initialSize = this.items.size();
            this.items.removeIf(item -> item.getId().equals(cartItemId));
            removed = this.items.size() < initialSize;
        }

        if (!removed) {
            throw new CustomException(CART_ITEM_NOT_FOUND);
        }
        recalculateTotalPrice();
    }

    public void clear() {
        this.items.clear();
        this.totalPrice = BigDecimal.ZERO;
    }


    private Optional<CartItem> findCartItemByProduct(Product product) {
        return this.items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();
    }

    private Optional<CartItem> findCartItemById(Long cartItemId) {
        if (this.items == null) {
            return Optional.empty();
        }
        return this.items.stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst();
    }

    private void recalculateTotalPrice() {
        this.totalPrice = this.items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
