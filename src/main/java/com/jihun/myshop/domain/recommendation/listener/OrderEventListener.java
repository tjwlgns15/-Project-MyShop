package com.jihun.myshop.domain.recommendation.listener;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.OrderItem;
import com.jihun.myshop.domain.order.event.OrderCompletedEvent;
import com.jihun.myshop.domain.recommendation.service.UserActivityTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final UserActivityTracker userActivityTracker;

    @EventListener
    public void handleOrderCompletedEvent(OrderCompletedEvent event) {
        Order order = event.getOrder();

        for (OrderItem orderItem : order.getOrderItems()) {
            userActivityTracker.trackProductPurchase(
                    order.getUser(),
                    orderItem.getProduct(),
                    orderItem.getQuantity());
        }
    }
}