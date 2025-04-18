package com.jihun.myshop.domain.order.controller.api;

import com.jihun.myshop.domain.order.entity.PaymentStatus;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentCancelDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentCreateDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentResponseDto;
import com.jihun.myshop.domain.order.service.OrderService;
import com.jihun.myshop.domain.order.service.PaymentService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
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

    // 결제 검증
//    @PostMapping("/verify/{imp_uid}")
//    public ApiResponseEntity<PaymentResponseDto> verifyPayment(@PathVariable String imp_uid,
//                                                               @RequestBody PaymentVerifyDto verifyDto) {
//        try {
//            // 1. 포트원 서버에서 결제 정보 조회
//            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(imp_uid);
//            Payment payment = iamportResponse.getResponse();
//
//            // 2. 결제 금액 검증
//            BigDecimal paidAmount = new BigDecimal(payment.getAmount().toString());
//            if (!paidAmount.equals(verifyDto.getAmount())) {
//                // 금액이 다를 경우 결제 취소
//                CancelData cancelData = new CancelData(imp_uid, true);
//                cancelData.setReason("결제 금액 불일치");
//                iamportClient.cancelPaymentByImpUid(cancelData);
//                throw new RuntimeException("결제 금액이 일치하지 않습니다.");
//            }
//
//            // 3. 결제 완료 처리
//            PaymentResponseDto response = paymentService.completePayment(
//                    verifyDto.getPaymentId(),
//                    payment.getMerchantUid(),
//                    imp_uid
//            );
//
//            return ApiResponseEntity.success(response);
//        } catch (IamportResponseException e) {
//            throw new RuntimeException("포트원 결제 검증 실패: " + e.getMessage());
//        } catch (IOException e) {
//            throw new RuntimeException("포트원 서버 통신 실패: " + e.getMessage());
//        }
//    }
    @PostMapping("/verify/{imp_uid}")
    public ApiResponseEntity<PaymentResponseDto> verifyPayment(@PathVariable String imp_uid,
                                                               @RequestBody PaymentVerifyDto verifyDto) {
        try {
            // 1. 포트원 서버에서 결제 정보 조회
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(imp_uid);
            Payment payment = iamportResponse.getResponse();

            // 2. 결제 금액 검증 (compareTo 사용하여 값만 비교)
            BigDecimal paidAmount = new BigDecimal(payment.getAmount().toString());
            // 로깅 추가로 디버깅 용이하게
            log.info("결제 금액 비교 - Iamport 금액: {}, 요청 금액: {}", paidAmount, verifyDto.getAmount());

            if (paidAmount.compareTo(verifyDto.getAmount()) != 0) {
                // 금액이 다를 경우 결제 취소
                CancelData cancelData = new CancelData(imp_uid, true);
                cancelData.setReason("결제 금액 불일치");
                iamportClient.cancelPaymentByImpUid(cancelData);
                throw new RuntimeException("결제 금액이 일치하지 않습니다.");
            }

            // 3. 결제 완료 처리
            PaymentResponseDto response = paymentService.completePayment(
                    verifyDto.getPaymentId(),
                    payment.getMerchantUid(),
                    imp_uid
            );

            return ApiResponseEntity.success(response);
        } catch (IamportResponseException e) {
            throw new RuntimeException("포트원 결제 검증 실패: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("포트원 서버 통신 실패: " + e.getMessage());
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
            // 1. 저장된 결제 정보 조회
            PaymentResponseDto paymentInfo = paymentService.getPayment(paymentId);

            // 2. 포트원 서버에 결제 취소 요청
            if (paymentInfo.getImpUid() != null && !paymentInfo.getImpUid().isEmpty()) {
                CancelData cancelData = new CancelData(paymentInfo.getImpUid(), true);
                cancelData.setReason(cancelDto.getReason());
                iamportClient.cancelPaymentByImpUid(cancelData);
            }

            // 3. 취소 처리
            PaymentResponseDto response = paymentService.cancelPayment(paymentId, cancelDto.getReason());
            return ApiResponseEntity.success(response);
        } catch (IamportResponseException e) {
            throw new RuntimeException("포트원 결제 취소 실패: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException("포트원 서버 통신 실패: " + e.getMessage());
        }
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

    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody WebhookDto webhookDto) {
        try {
            // 웹훅 요청 검증
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(webhookDto.getImp_uid());
            Payment payment = iamportResponse.getResponse();

            // 결제 상태에 따른 처리
            if ("paid".equals(payment.getStatus())) {
                // 결제 완료 처리
                paymentService.processWebhookPaymentComplete(payment.getMerchantUid(), webhookDto.getImp_uid());
            } else if ("cancelled".equals(payment.getStatus())) { // Iamport API에 지정되어있는 값이 "cancelled"여서 바꾸면 안 됨
                // 결제 취소 처리
                paymentService.processWebhookPaymentCancel(payment.getMerchantUid(), "포트원 웹훅: 결제 취소");
            } else if ("failed".equals(payment.getStatus())) {
                // 결제 실패 처리
                paymentService.processWebhookPaymentFail(payment.getMerchantUid(), "포트원 웹훅: 결제 실패");
            }
        } catch (IamportResponseException | IOException e) {
            // 로그 기록
            System.err.println("웹훅 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
