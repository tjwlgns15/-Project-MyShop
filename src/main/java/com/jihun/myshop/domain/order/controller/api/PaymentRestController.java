package com.jihun.myshop.domain.order.controller.api;

import com.jihun.myshop.domain.order.entity.PaymentStatus;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentCancelDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentCreateDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentResponseDto;
import com.jihun.myshop.domain.order.service.PaymentService;
import com.jihun.myshop.domain.product.service.ProductService;
import com.jihun.myshop.global.common.ApiResponseEntity;
import com.jihun.myshop.global.common.CustomPageRequest;
import com.jihun.myshop.global.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.jihun.myshop.domain.order.entity.dto.PaymentDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentRestController {

    private final PaymentService paymentService;


    @PostMapping("/new")
    public ApiResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentCreateDto paymentCreateDto) {
        PaymentResponseDto response = paymentService.createPayment(paymentCreateDto);
        return ApiResponseEntity.success(response);
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

    // 결제 조회(paymentKey)
    @GetMapping("/key/{paymentKey}")
    public ApiResponseEntity<PaymentResponseDto> getPaymentByPaymentKey(@PathVariable String paymentKey) {
        PaymentResponseDto response = paymentService.getPaymentByPaymentKey(paymentKey);
        return ApiResponseEntity.success(response);
    }

    // 결제 완료
    @PostMapping("/{paymentId}/complete")
    public ApiResponseEntity<PaymentResponseDto> completePayment(@PathVariable Long paymentId,
                                                                 @RequestBody PaymentCompleteDto completeDto) {
        PaymentResponseDto response = paymentService.completePayment(paymentId, completeDto.getPaymentKey());
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
        PaymentResponseDto response = paymentService.cancelPayment(paymentId, cancelDto.getReason());
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
