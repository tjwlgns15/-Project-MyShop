package com.jihun.myshop.domain.cart.event;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.cart.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CartItemAddedEvent {
    private final Cart cart;
    private final CartItem cartItem;

}