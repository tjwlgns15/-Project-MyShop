package com.jihun.myshop.domain.recommendation.listener;
import com.jihun.myshop.domain.recommendation.service.UserActivityTracker;
import com.jihun.myshop.domain.wishlist.entity.WishlistItem;
import com.jihun.myshop.domain.wishlist.event.WishlistItemAddedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WishlistEventListener {

    private final UserActivityTracker userActivityTracker;

    @EventListener
    public void handleWishlistItemAddedEvent(WishlistItemAddedEvent event) {
        WishlistItem wishlistItem = event.getWishlistItem();

        userActivityTracker.trackProductAddedToWishlist(
                wishlistItem.getUser(),
                wishlistItem.getProduct());
    }
}