package com.jihun.myshop.domain.recommendation.listener;

import com.jihun.myshop.domain.product.entity.Review;
import com.jihun.myshop.domain.product.event.ReviewAddedEvent;
import com.jihun.myshop.domain.recommendation.service.UserActivityTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewEventListener {

    private final UserActivityTracker userActivityTracker;

    @EventListener
    public void handleReviewAddedEvent(ReviewAddedEvent event) {
        Review review = event.getReview();
        userActivityTracker.trackProductReviewed(review);
    }
}
