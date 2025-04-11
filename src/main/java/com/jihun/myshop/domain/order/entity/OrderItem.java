package com.jihun.myshop.domain.order.entity;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal finalPrice;
    private String productSnapshot; // 주문 시점의 상품 정보 스냅샷 (JSON 형태로 저장)


    public static OrderItem createOrderItem(Product product, int quantity) {
        BigDecimal price = product.getPrice();

        BigDecimal discount = BigDecimal.ZERO;
        if (product.getDiscountPrice() != null) {
            discount = price.subtract(product.getDiscountPrice()).multiply(BigDecimal.valueOf(quantity));
        }

        BigDecimal finalPrice;
        if (product.getDiscountPrice() != null) {
            finalPrice = product.getDiscountPrice().multiply(BigDecimal.valueOf(quantity));
        } else {
            finalPrice = price.multiply(BigDecimal.valueOf(quantity));
        }

        String productSnapshot = createProductSnapshot(product);

        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .price(price)  // 단가 (할인 전)
                .discount(discount)  // 총 할인액
                .finalPrice(finalPrice)  // 최종 가격 (할인 후 가격 * 수량)
                .productSnapshot(productSnapshot)
                .build();
    }

    private static String createProductSnapshot(Product product) {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"price\":%s,\"discountPrice\":%s}",
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDiscountPrice() != null ? product.getDiscountPrice() : "null"
        );
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
