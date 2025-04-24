package com.jihun.myshop.domain.recommendation.listener;

import com.jihun.myshop.domain.product.event.ProductViewEvent;
import com.jihun.myshop.domain.recommendation.service.UserActivityTracker;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductViewEventListener {
    private final UserActivityTracker userActivityTracker;

    @EventListener
    public void handleCartItemAddedEvent(ProductViewEvent event) {
        CustomUserDetails user = event.getUser();
        Long productId = event.getProductId();

        userActivityTracker.trackProductView(user, productId);
    }
}