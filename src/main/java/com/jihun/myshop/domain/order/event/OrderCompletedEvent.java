package com.jihun.myshop.domain.order.event;

import com.jihun.myshop.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCompletedEvent {
    private final Order order;
}