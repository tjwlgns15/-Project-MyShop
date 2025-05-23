package com.jihun.myshop.domain.order.controller.api;

import com.jihun.myshop.domain.order.entity.OrderStatus;
import com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderCreateDto;
import com.jihun.myshop.domain.order.service.OrderService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.dto.CustomPageRequest;
import com.jihun.myshop.global.common.dto.CustomPageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jihun.myshop.domain.order.entity.dto.OrderDto.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orderService;

    @PostMapping("/new")
    public ApiResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateDto orderCreateDto,
                                                           @AuthenticationPrincipal CustomUserDetails currentUser) {

        OrderResponseDto orderResponseDto = orderService.createOrder(orderCreateDto, currentUser);
        return ApiResponseEntity.success(orderResponseDto);
    }

    @GetMapping("/{orderId}")
    public ApiResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        OrderResponseDto orderResponseDto = orderService.getOrder(orderId);
        return ApiResponseEntity.success(orderResponseDto);
    }

    // 주문번호로 주문 조회
    @GetMapping("/number/{orderNumber}")
    public ApiResponseEntity<OrderResponseDto> getOrderByNumber(@PathVariable String orderNumber) {
        OrderResponseDto response = orderService.getOrderByOrderNumber(orderNumber);
        return ApiResponseEntity.success(response);
    }

    // 사용자용
    @GetMapping("/my")
    public ApiResponseEntity<CustomPageResponse<OrderResponseDto>> getMyOrders(CustomPageRequest pageRequest,
                                                                               @AuthenticationPrincipal CustomUserDetails currentUser) {
        CustomPageResponse<OrderResponseDto> response = orderService.getUserOrders(currentUser.getId(), pageRequest);
        return ApiResponseEntity.success(response);
    }

    // 관리자용: 특정 사용자의 주문 목록 조회 (관리자 권한 체크 필요)
    @GetMapping("/user/{userId}")
    public ApiResponseEntity<CustomPageResponse<OrderResponseDto>> getUserOrders(@PathVariable Long userId,
                                                                                 CustomPageRequest pageRequest,
                                                                                 @AuthenticationPrincipal CustomUserDetails currentUser) {
        CustomPageResponse<OrderResponseDto> response = orderService.getUserOrdersToAdmin(userId, pageRequest, currentUser);
        return ApiResponseEntity.success(response);
    }

    // 주문 상태 업데이트
    @PatchMapping("/{orderId}/status")
    public ApiResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateDto statusUpdateDto) {
        OrderResponseDto response = orderService.updateOrderStatus(orderId, statusUpdateDto.getOrderStatus());
        return ApiResponseEntity.success(response);
    }

    // 주문 취소
    @PostMapping("/{orderId}/cancel")
    public ApiResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable Long orderId,
            @RequestBody OrderCancelDto cancelDto) {
        OrderResponseDto response = orderService.cancelOrder(orderId, cancelDto);
        return ApiResponseEntity.success(response);
    }

    // 배송 추적 번호 설정
    @PatchMapping("/{orderId}/tracking")
    public ApiResponseEntity<OrderResponseDto> setTrackingNumber(@PathVariable Long orderId,
                                                                 @RequestBody TrackingNumberDto trackingNumberDto) {
        OrderResponseDto response = orderService.setTrackingNumber(orderId, trackingNumberDto.getTrackingNumber());
        return ApiResponseEntity.success(response);
    }


    // 특정 상태의 주문 목록 조회
    @GetMapping("/my/{statuses}")
    public ApiResponseEntity<CustomPageResponse<OrderResponseDto>> getOrdersByStatusToUser(@PathVariable List<OrderStatus> statuses,
                                                                                           CustomPageRequest pageRequest,
                                                                                           @AuthenticationPrincipal CustomUserDetails currentUser) {
        CustomPageResponse<OrderResponseDto> response = orderService.getOrdersByStatusToUser(statuses, pageRequest, currentUser);
        return ApiResponseEntity.success(response);
    }

    @GetMapping("/admin/{statuses}")
    public ApiResponseEntity<CustomPageResponse<OrderResponseDto>> getOrdersByStatusToAdmin(@PathVariable List<OrderStatus> statuses,
                                                                                            CustomPageRequest pageRequest,
                                                                                            @AuthenticationPrincipal CustomUserDetails currentUser) {
        CustomPageResponse<OrderResponseDto> response = orderService.getOrdersByStatusToAdmin(statuses, pageRequest, currentUser);
        return ApiResponseEntity.success(response);
    }

    // 주문 상태별 주문 건수 조회
    @GetMapping("/count/status/{status}")
    public ApiResponseEntity<Long> countOrdersByStatus(@PathVariable OrderStatus status,
                                                       @AuthenticationPrincipal CustomUserDetails currentUser) {
        long count = orderService.countOrdersByStatus(status, currentUser);
        return ApiResponseEntity.success(count);
    }
}
