package com.jihun.myshop.domain.order.service;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.Payment;
import com.jihun.myshop.domain.order.entity.PaymentStatus;
import com.jihun.myshop.domain.order.entity.mapper.PaymentMapper;
import com.jihun.myshop.domain.order.repository.OrderRepository;
import com.jihun.myshop.domain.order.repository.PaymentRepository;
import com.jihun.myshop.global.common.dto.CustomPageRequest;
import com.jihun.myshop.global.common.dto.CustomPageResponse;
import com.jihun.myshop.global.exception.CustomException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.jihun.myshop.domain.order.entity.OrderStatus.CANCELED;
import static com.jihun.myshop.domain.order.entity.OrderStatus.PAID;
import static com.jihun.myshop.domain.order.entity.dto.PaymentDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final OrderService orderService;


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

        orderService.completeOrderPayment(payment.getOrder().getId());

        return paymentMapper.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto failPayment(Long paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);
        payment.markAsFailed(reason);

        orderService.rollbackOrderPayment(payment.getOrder().getId(), reason);

        return paymentMapper.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto cancelPayment(Long paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);
        payment.markAsCanceled(reason);

        orderService.rollbackOrderPayment(payment.getOrder().getId(), reason);

        return paymentMapper.fromEntity(payment);
    }

    public CustomPageResponse<PaymentResponseDto> getPaymentsByStatus(List<PaymentStatus> statuses, CustomPageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageRequest();

        Page<Payment> paymentPage = paymentRepository.findByPaymentStatusIn(statuses, pageable);
        Page<PaymentResponseDto> responsePage = paymentPage.map(paymentMapper::fromEntity);
        return CustomPageResponse.fromPage(responsePage);
    }

    public CustomPageResponse<PaymentResponseDto> getPaymentsByUserId(Long userId, CustomPageRequest pageRequest) {
        Pageable pageable = pageRequest.toPageRequest();

        Page<Payment> paymentPage = paymentRepository.findByUserId(userId, pageable);
        Page<PaymentResponseDto> responsePage = paymentPage.map(paymentMapper::fromEntity);
        return CustomPageResponse.fromPage(responsePage);
    }

    public long countPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.countByPaymentStatus(status);
    }

    @Transactional
    public PaymentResponseDto verifyAndCompletePayment(String impUid, PaymentVerifyDto verifyDto, IamportClient iamportClient) throws IamportResponseException, IOException {
        // 포트원 서버에서 결제 정보 조회
        IamportResponse<com.siot.IamportRestClient.response.Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);
        com.siot.IamportRestClient.response.Payment portonePayment = iamportResponse.getResponse();

        // 결제 금액 검증
        BigDecimal paidAmount = new BigDecimal(portonePayment.getAmount().toString());
        log.info("결제 금액 비교 - PG사 결제 금액: {}, 클라이언트 요청 금액: {}", paidAmount, verifyDto.getAmount());

        // 클라이언트 요청 금액과 PG사 결제 금액 비교
        if (paidAmount.compareTo(verifyDto.getAmount()) != 0) {
            // 금액이 다를 경우 결제 취소
            CancelData cancelData = new CancelData(impUid, true);
            cancelData.setReason("결제 금액 불일치 (클라이언트 요청과 PG사 결제 금액)");
            iamportClient.cancelPaymentByImpUid(cancelData);
            throw new CustomException(PAYMENT_AMOUNT_MISMATCH);
        }

        // 주문 존재 여부 및 상태 확인
        Order order = getOrderById(verifyDto.getOrderId());

        // 주문 금액과 결제 금액 비교
        BigDecimal orderAmount = order.getFinalAmount();
        log.info("주문 금액 비교 - 주문 금액: {}, 결제 금액: {}", orderAmount, paidAmount);

        if (orderAmount.compareTo(paidAmount) != 0) {
            log.error("결제 금액 불일치: 주문금액={}, 결제금액={}", orderAmount, paidAmount);
            // 이미 PG사에 결제 취소 요청을 보냈으므로 별도 취소 요청 불필요
            throw new CustomException(PAYMENT_AMOUNT_MISMATCH);
        }

        // 결제 정보 업데이트
        Payment payment = getPaymentById(verifyDto.getPaymentId());
        payment.markAsCompleted(portonePayment.getMerchantUid(), impUid);

        // OrderService를 통해 주문 상태 업데이트
        orderService.completeOrderPayment(order.getId());

        log.info("결제 검증 및 완료 처리 성공: orderId={}, impUid={}", order.getId(), impUid);

        return paymentMapper.fromEntity(payment);
    }

    @Transactional
    public PaymentResponseDto cancelPaymentWithPortone(Long paymentId, String reason, IamportClient iamportClient) throws IamportResponseException, IOException {
        // 1. 저장된 결제 정보 조회
        Payment payment = getPaymentById(paymentId);

        // 2. 포트원 서버에 결제 취소 요청
        if (payment.getImpUid() != null && !payment.getImpUid().isEmpty()) {
            CancelData cancelData = new CancelData(payment.getImpUid(), true);
            cancelData.setReason(reason);
            iamportClient.cancelPaymentByImpUid(cancelData);
        }

        // 3. 취소 처리
        return cancelPayment(paymentId, reason);
    }

    @Transactional
    public void processWebhookEvent(String impUid, String merchantUid, String status) {
        try {
            Order order = orderRepository.findByOrderNumber(merchantUid)
                    .orElseThrow(() -> new CustomException(ORDER_NOT_FOUND));

            Payment payment = paymentRepository.findByOrder(order)
                    .orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));

            switch (status) {
                case "paid" -> {
                    // 결제 완료 처리 - Setter 대신 도메인 메서드 사용
                    payment.markAsCompleted(merchantUid, impUid);

                    // OrderService를 통해 주문 상태 업데이트
                    orderService.processOrderByStatus(merchantUid, PAID, null);
                    log.info("웹훅 결제 완료 처리: orderNumber={}, impUid={}", merchantUid, impUid);
                }
                case "cancelled" -> {
                    // 결제 취소 처리 - Setter 대신 도메인 메서드 사용
                    payment.markAsCanceled("포트원 웹훅: 결제 취소");

                    // OrderService를 통해 주문 취소 처리
                    orderService.processOrderByStatus(merchantUid, CANCELED, "포트원 웹훅: 결제 취소");
                    log.info("웹훅 결제 취소 처리: orderNumber={}, impUid={}", merchantUid, impUid);
                }
                case "failed" -> {
                    // 결제 실패 처리 - Setter 대신 도메인 메서드 사용
                    payment.markAsFailed("포트원 웹훅: 결제 실패");

                    // OrderService를 통해 주문 취소 처리
                    orderService.processOrderByStatus(merchantUid, CANCELED, "포트원 웹훅: 결제 실패");
                    log.info("웹훅 결제 실패 처리: orderNumber={}, impUid={}", merchantUid, impUid);
                }
                default -> log.warn("알 수 없는 결제 상태: {}, orderNumber={}, impUid={}", status, merchantUid, impUid);
            }
        } catch (Exception e) {
            log.error("웹훅 처리 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }


}
