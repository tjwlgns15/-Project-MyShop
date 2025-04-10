package com.jihun.myshop.domain.product.service;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.exception.ErrorCode;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class ProductAuthorizationService {

    /**
     * 사용자가 상품을 수정/삭제할 권한이 있는지 확인합니다.
     * - ROLE_ADMIN 권한을 가진 사용자는 모든 상품을 수정/삭제할 수 있습니다.
     * - 상품의 판매자(seller)는 자신의 상품을 수정/삭제할 수 있습니다.
     *
     * @param product 대상 상품
     * @param userDetails 현재 사용자 정보
     * @throws CustomException 권한이 없을 경우 예외 발생
     */
    public void validateProductAuthorization(Product product, CustomUserDetails userDetails) {
        // 관리자인 경우 항상 권한 허용
        if (hasAdminRole(userDetails)) {
            return;
        }

        // 본인(판매자)인 경우 권한 허용
        if (product.getSeller() != null && product.getSeller().getId().equals(userDetails.getId())) {
            return;
        }

        // 그 외의 경우 권한 없음 예외 발생
        throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    /**
     * 사용자가 관리자 권한을 가지고 있는지 확인합니다.
     *
     * @param userDetails 사용자 정보
     * @return 관리자 권한 보유 여부
     */
    private boolean hasAdminRole(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}