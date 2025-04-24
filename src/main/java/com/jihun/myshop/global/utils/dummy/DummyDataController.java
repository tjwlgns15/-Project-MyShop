package com.jihun.myshop.global.utils.dummy;

import com.jihun.myshop.global.common.ApiResponseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 더미 데이터 생성을 위한 컨트롤러
 * URL: /api/admin/dummy (POST)
 */
@RestController
@RequestMapping("/api/admin/dummy")
@RequiredArgsConstructor
@Slf4j
public class DummyDataController {

    private final DummyDataGenerator dummyDataGenerator;
    private final UserActionGenerator userActionGenerator;

    /**
     * 더미 데이터 생성 API
     * seller / 1234 계정으로 로그인 후 데이터 생성
     * @return 생성된 상품 수를 담은 응답
     */
    @PostMapping("/products")
    public ApiResponseEntity<Map<String, Object>> generateDummyData() {
        try {
            int productCount = dummyDataGenerator.generateDummyData();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "더미 데이터 생성 완료");
            response.put("productCount", productCount);

            return ApiResponseEntity.success(response);
        } catch (Exception e) {
            log.error("더미 데이터 생성 중 오류 발생", e);
            return ApiResponseEntity.error("더미 데이터 생성 실패: " + e.getMessage());
        }
    }

    /**
     * user 계정으로 활동 데이터 생성 API
     * - 주문 5개
     * - 리뷰 2개
     * - 장바구니 3개
     * - 위시리스트 3개
     * @return 생성된 데이터 수를 담은 응답
     */
    @PostMapping("/user-act")
    public ApiResponseEntity<Map<String, Object>> generateUserActions() {
        try {
            Map<String, Object> results = userActionGenerator.generateUserActions();

            return ApiResponseEntity.success(results);
        } catch (Exception e) {
            log.error("사용자 활동 데이터 생성 중 오류 발생", e);
            return ApiResponseEntity.error("사용자 활동 데이터 생성 실패: " + e.getMessage());
        }
    }
}