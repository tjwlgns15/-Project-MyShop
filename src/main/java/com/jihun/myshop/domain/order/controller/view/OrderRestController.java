package com.jihun.myshop.domain.order.controller.view;

import com.jihun.myshop.domain.order.entity.dto.OrderDto;
import com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderCreate;
import com.jihun.myshop.domain.order.service.OrderService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.jihun.myshop.domain.order.entity.dto.OrderDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping("/new")
    public ApiResponseEntity<OrderResponse> createOrder(@RequestBody OrderCreate orderCreate,
                                                        @AuthenticationPrincipal CustomUserDetails currentUser) {

        OrderResponse orderResponse = orderService.createOrder(orderCreate, currentUser);
        return ApiResponseEntity.success(orderResponse);
    }
}
