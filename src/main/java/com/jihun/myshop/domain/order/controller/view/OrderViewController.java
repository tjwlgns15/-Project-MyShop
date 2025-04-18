package com.jihun.myshop.domain.order.controller.view;

import com.jihun.myshop.domain.order.entity.OrderStatus;
import com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderResponseDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentCreateDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentResponseDto;
import com.jihun.myshop.domain.order.service.OrderService;
import com.jihun.myshop.domain.order.service.PaymentService;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jihun.myshop.domain.order.entity.PaymentMethod.CREDIT_CARD;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderViewController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    // 주문 목록 페이지
    @GetMapping
    public String orders(@ModelAttribute CustomPageRequest pageRequest,
                         @AuthenticationPrincipal CustomUserDetails currentUser,
                         Model model) {
        // 사용자의 주문 목록 조회
        PageResponse<OrderResponseDto> orderList = orderService.getUserOrders(currentUser.getId(), pageRequest);
        model.addAttribute("orderList", orderList);
        return "domain/order/order/order-list";
    }

    @GetMapping("/my/{status}")
    public String getOrdersByStatus(@PathVariable OrderStatus status,
                                    @ModelAttribute CustomPageRequest pageRequest,
                                    @AuthenticationPrincipal CustomUserDetails currentUser,
                                    Model model) {
        // 특정 상태의 주문 목록 조회
        PageResponse<OrderResponseDto> orderList = orderService.getOrdersByStatusToUser(
                List.of(status), pageRequest, currentUser);
        model.addAttribute("orderList", orderList);
        model.addAttribute("currentStatus", status.name());
        return "domain/order/order/order-list";
    }

    // 결제 대기 중인 주문 목록 페이지
    @GetMapping("/payment-pending")
    public String paymentPendingOrders(@ModelAttribute CustomPageRequest pageRequest,
                                       @AuthenticationPrincipal CustomUserDetails currentUser,
                                       Model model) {
        // 결제 대기 중인 주문 목록 조회
        PageResponse<OrderResponseDto> orderList = orderService.getOrdersByStatusToUser(
                List.of(OrderStatus.PAYMENT_PENDING), pageRequest, currentUser);
        model.addAttribute("orderList", orderList);
        return "domain/order/order/payment-pending";
    }

    // 주문 상세 페이지
    @GetMapping("/{orderId}")
    public String orderDetail(@PathVariable Long orderId, Model model) {
        OrderResponseDto order = orderService.getOrder(orderId);
        model.addAttribute("order", order);
        return "domain/order/order/order-detail";
    }

    // 결제 페이지
    @GetMapping("/{orderId}/payment")
    public String payment(@PathVariable Long orderId,
                          @AuthenticationPrincipal CustomUserDetails currentUser,
                          Model model) {
        OrderResponseDto order = orderService.getOrder(orderId);

        // 이미 결제된 주문인지 확인
        if (order.getOrderStatus() == OrderStatus.PAID) {
            return "redirect:/orders/" + orderId;
        }

        // 기존 결제 정보가 있는지 확인
        PaymentResponseDto payment = null;
        try {
            payment = paymentService.getPaymentByOrderId(orderId);
        } catch (Exception e) {
            // 결제 정보가 없는 경우, 새로 생성

            PaymentCreateDto createDto = PaymentCreateDto.builder()
                    .orderId(orderId)
                    .paymentMethod(CREDIT_CARD)
                    .build();

            payment = paymentService.createPayment(createDto);
        }

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        model.addAttribute("userEmail", currentUser.getUsername());

        return "domain/order/payment/payment";
    }

    // 결제 완료 페이지
    @GetMapping("/complete")
    public String orderComplete(@RequestParam("orderId") Long orderId, Model model) {
        OrderResponseDto order = orderService.getOrder(orderId);

        PaymentResponseDto payment = null;
        try {
            payment = paymentService.getPaymentByOrderId(orderId);
        } catch (Exception e) {
            // 결제 정보가 없는 경우 처리
            return "redirect:/orders/" + orderId;
        }

        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        return "domain/order/payment/payment-complete";
    }


}