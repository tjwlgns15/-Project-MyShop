package com.jihun.myshop.domain.order.service;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.Payment;
import com.jihun.myshop.domain.order.entity.PaymentStatus;
import com.jihun.myshop.domain.order.entity.mapper.PaymentMapper;
import com.jihun.myshop.domain.order.repository.OrderRepository;
import com.jihun.myshop.domain.order.repository.PaymentRepository;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jihun.myshop.domain.order.entity.dto.PaymentDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;


    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));
    }
    private Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }
    private Payment getPaymentByOrder(Order order) {
        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }
    private Payment findPaymentByMerchantUid(String merchantUid) {
        return paymentRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));
    }


    @Transactional
    public PaymentResponseDto createPayment(PaymentCreateDto paymentCreateDto) {
        Order order = getOrderById(paymentCreateDto.getOrderId());

        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new CustomException(PAYMENT_ALREADY_EXIST);
        }

        Payment payment = Payment.createPayment(order, paymentCreateDto.getPaymentMethod());
        Payment savedPayment = paymentRepository.save(payment);

        return paymentMapper.fromEntity(savedPayment);
    }

    public PaymentResponseDto getPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        return paymentMapper.fromEntity(payment);
    }

    public PaymentResponseDto getPaymentByOrderId(Long orderId) {
        Order order = getOrderById(orderId);

        Payment payment = getPaymentByOrder(order);
        return paymentMapper.fromEntity(payment);
    }

    public PaymentResponseDto getPaymentByMerchantUid(String merchantUid) {
        Payment payment = findPaymentByMerchantUid(merchantUid);
        return paymentMapper.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto completePayment(Long paymentId, String merchantUid, String impUid) {
        Payment payment = getPaymentById(paymentId);
        payment.markAsCompleted(merchantUid, impUid);
        return paymentMapper.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto failPayment(Long paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);
        payment.markAsFailed(reason);
        return paymentMapper.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto cancelPayment(Long paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);
        payment.markAsCanceled(reason);
        return paymentMapper.fromEntity(payment);
    }

    public PageResponse<PaymentResponseDto> getPaymentsByStatus(List<PaymentStatus> statuses, CustomPageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageRequest();

        Page<Payment> paymentPage = paymentRepository.findByPaymentStatusIn(statuses, pageable);
        Page<PaymentResponseDto> responsePage = paymentPage.map(paymentMapper::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    public PageResponse<PaymentResponseDto> getPaymentsByUserId(Long userId, CustomPageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageRequest();

        Page<Payment> paymentPage = paymentRepository.findByUserId(userId, pageable);
        Page<PaymentResponseDto> responsePage = paymentPage.map(paymentMapper::fromEntity);
        return PageResponse.fromPage(responsePage);
    }

    public long countPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.countByPaymentStatus(status);
    }

    @Transactional
    public PaymentResponseDto processWebhookPaymentComplete(String merchantUid, String impUid) {
        // merchantUid를 paymentKey로 사용한다고 가정
        Payment payment = findPaymentByMerchantUid(merchantUid);
        payment.markAsCompleted(merchantUid, impUid);
        return paymentMapper.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto processWebhookPaymentCancel(String merchantUid, String reason) {
        Payment payment = findPaymentByMerchantUid(merchantUid);
        payment.markAsCanceled(reason);
        return paymentMapper.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto processWebhookPaymentFail(String merchantUid, String reason) {
        Payment payment = findPaymentByMerchantUid(merchantUid);
        payment.markAsFailed(reason);
        return paymentMapper.fromEntity(payment);
    }
}
