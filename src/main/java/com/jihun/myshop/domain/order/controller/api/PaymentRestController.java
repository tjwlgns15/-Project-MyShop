package com.jihun.myshop.domain.order.controller.api;

import com.jihun.myshop.domain.order.entity.PaymentStatus;
import com.jihun.myshop.domain.order.entity.dto.OrderDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentCancelDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentCreateDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentResponseDto;
import com.jihun.myshop.domain.order.service.OrderService;
import com.jihun.myshop.domain.order.service.PaymentService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import com.jihun.myshop.global.exception.CustomException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.jihun.myshop.domain.order.entity.dto.PaymentDto.*;
import static com.jihun.myshop.global.exception.ErrorCode.PAYMENT_VERIFICATION_FAILED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Slf4j
public class PaymentRestController {

    @Value("${portone.imp-key}")
    private String restApiKey;
    @Value("${portone.imp-secret}")
    private String restApiSecret;
    private IamportClient iamportClient;

    private final PaymentService paymentService;
    private final OrderService orderService;

    // import 초기화
    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(restApiKey, restApiSecret);
    }

    @PostMapping("/new")
    public ApiResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentCreateDto paymentCreateDto) {
        PaymentResponseDto response = paymentService.createPayment(paymentCreateDto);
        return ApiResponseEntity.success(response);
    }

    @PostMapping("/verify/{imp_uid}")
    public ApiResponseEntity<PaymentResponseDto> verifyPayment(@PathVariable String imp_uid,
                                                               @RequestBody PaymentVerifyDto verifyDto) {
        try {
            PaymentResponseDto response = paymentService.verifyAndCompletePayment(imp_uid, verifyDto, iamportClient);

            return ApiResponseEntity.success(response);
        } catch (IamportResponseException e) {
            log.error("포트원 결제 검증 실패: {}", e.getMessage(), e);
            paymentService.failPayment(verifyDto.getPaymentId(), "포트원 검증 실패: " + e.getMessage());
            throw new CustomException(PAYMENT_VERIFICATION_FAILED);
        } catch (IOException e) {
            log.error("포트원 서버 통신 실패: {}", e.getMessage(), e);
            paymentService.failPayment(verifyDto.getPaymentId(), "포트원 서버 통신 실패: " + e.getMessage());
            throw new CustomException(PAYMENT_VERIFICATION_FAILED);
        }
    }

    // 결제 완료
    @PostMapping("/{paymentId}/complete")
    public ApiResponseEntity<PaymentResponseDto> completePayment(@PathVariable Long paymentId,
                                                                 @RequestBody PaymentCompleteDto completeDto) {
        PaymentResponseDto response = paymentService.completePayment(paymentId, completeDto.getMerchantUid(), completeDto.getImpUid());
        return ApiResponseEntity.success(response);
    }

    // 결제 실패
    @PostMapping("/{paymentId}/fail")
    public ApiResponseEntity<PaymentResponseDto> failPayment(@PathVariable Long paymentId,
                                                             @RequestBody PaymentCancelDto cancelDto) {
        PaymentResponseDto response = paymentService.failPayment(paymentId, cancelDto.getReason());
        return ApiResponseEntity.success(response);
    }

    // 결제 취소
    @PostMapping("/{paymentId}/cancel")
    public ApiResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable Long paymentId,
                                                               @RequestBody PaymentCancelDto cancelDto) {
        try {
            PaymentResponseDto response = paymentService.cancelPaymentWithPortone(paymentId, cancelDto.getReason(), iamportClient);
            return ApiResponseEntity.success(response);
        } catch (IamportResponseException e) {
            log.error("포트원 결제 취소 실패: {}", e.getMessage(), e);
            throw new CustomException(PAYMENT_VERIFICATION_FAILED);
        } catch (IOException e) {
            log.error("포트원 서버 통신 실패: {}", e.getMessage(), e);
            throw new CustomException(PAYMENT_VERIFICATION_FAILED);
        }
    }

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody WebhookDto webhookDto) {
        try {
            // 웹훅 요청 검증
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(webhookDto.getImp_uid());
            Payment payment = iamportResponse.getResponse();

            // 통합된 웹훅 처리 메서드 호출
            paymentService.processWebhookEvent(
                    webhookDto.getImp_uid(),
                    payment.getMerchantUid(),
                    payment.getStatus()
            );
        } catch (IamportResponseException | IOException e) {
            // 로그 기록
            log.error("웹훅 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    // 결제 조회
    @GetMapping("/{paymentId}")
    public ApiResponseEntity<PaymentResponseDto> getPayment(@PathVariable Long paymentId) {
        PaymentResponseDto response = paymentService.getPayment(paymentId);
        return ApiResponseEntity.success(response);
    }

    // 결제 조회(orderId)
    @GetMapping("/order/{orderId}")
    public ApiResponseEntity<PaymentResponseDto> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentResponseDto response = paymentService.getPaymentByOrderId(orderId);
        return ApiResponseEntity.success(response);
    }

    // 결제 조회(merchantUid)
    @GetMapping("/key/{merchantUid}")
    public ApiResponseEntity<PaymentResponseDto> getPaymentByMerchantUid(@PathVariable String merchantUid) {
        PaymentResponseDto response = paymentService.getPaymentByMerchantUid(merchantUid);
        return ApiResponseEntity.success(response);
    }

    // 결제 목록 조회(paymentStatus)
    @GetMapping("/status/{statuses}")
    public ApiResponseEntity<PageResponse<PaymentResponseDto>> getPaymentsByStatus(@PathVariable List<PaymentStatus> statuses,
                                                                                   CustomPageRequest pageRequest) {
        PageResponse<PaymentResponseDto> response = paymentService.getPaymentsByStatus(statuses, pageRequest);
        return ApiResponseEntity.success(response);
    }

    // 결제 목록 조회(userId)
    @GetMapping("/user/{userId}")
    public ApiResponseEntity<PageResponse<PaymentResponseDto>> getPaymentsByUserId(@PathVariable Long userId,
                                                                                   CustomPageRequest pageRequest) {
        PageResponse<PaymentResponseDto> response = paymentService.getPaymentsByUserId(userId, pageRequest);
        return ApiResponseEntity.success(response);
    }

    // 결제 건수 조회(paymentStatus)
    @GetMapping("/count/status/{status}")
    public ApiResponseEntity<Long> countPaymentsByStatus(@PathVariable PaymentStatus status) {
        long count = paymentService.countPaymentsByStatus(status);
        return ApiResponseEntity.success(count);
    }
}
