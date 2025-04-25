package com.jihun.myshop.global.security.service;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.global.exception.CustomException;
import com.jihun.myshop.global.exception.ErrorCode;
import com.jihun.myshop.global.security.customUserDetails.CustomUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import static com.jihun.myshop.global.exception.ErrorCode.UNAUTHORIZED_ACCESS;

@Service
public class AuthorizationService {

    // 사용자의 특정 역할 확인
    public boolean hasRole(CustomUserDetails userDetails, String roleName) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roleName::equals);
    }

    // 관리자 권한 확인 (자주 사용되는 특수 케이스)
    public boolean isAdmin(CustomUserDetails userDetails) {
        return hasRole(userDetails, "ROLE_ADMIN");
    }

    public void validateAdmin(CustomUserDetails userDetails) {
        if (!isAdmin(userDetails)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }
    }

    public boolean isSeller(CustomUserDetails userDetails) {
        return hasRole(userDetails, "ROLE_SELLER");
    }
    public void validateSeller(CustomUserDetails userDetails) {
        if (!isSeller(userDetails)) {
            throw new CustomException(UNAUTHORIZED_ACCESS);
        }
    }

    // 상품 수정 권한 확인
    public boolean canModifyProduct(CustomUserDetails userDetails, Product product) {
        return isAdmin(userDetails) ||
                (product.getSeller() != null && product.getSeller().getId().equals(userDetails.getId()));
    }
}