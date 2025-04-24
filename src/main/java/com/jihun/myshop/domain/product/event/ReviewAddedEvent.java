package com.jihun.myshop.domain.product.event;

import com.jihun.myshop.domain.product.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewAddedEvent {
    private final Review review;
}