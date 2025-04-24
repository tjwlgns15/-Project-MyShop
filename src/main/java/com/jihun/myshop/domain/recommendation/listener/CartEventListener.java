package com.jihun.myshop.domain.recommendation.listener;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.cart.entity.CartItem;
import com.jihun.myshop.domain.cart.event.CartItemAddedEvent;
import com.jihun.myshop.domain.recommendation.service.UserActivityTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartEventListener {
    private final UserActivityTracker userActivityTracker;

    @EventListener
    public void handleCartItemAddedEvent(CartItemAddedEvent event) {
        Cart cart = event.getCart();
        CartItem cartItem = event.getCartItem();

        userActivityTracker.trackProductAddedToCart(cart, cartItem.getProduct());
    }
}
