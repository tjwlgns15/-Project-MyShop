package com.jihun.myshop.domain.wishlist.event;

import com.jihun.myshop.domain.wishlist.entity.WishlistItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WishlistItemAddedEvent {
    private final WishlistItem wishlistItem;
}