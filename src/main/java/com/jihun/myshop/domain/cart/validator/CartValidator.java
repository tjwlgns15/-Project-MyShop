package com.jihun.myshop.domain.cart.validator;

import com.jihun.myshop.domain.cart.entity.dto.CartItemDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemCreateDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemUpdateDto;
import com.jihun.myshop.domain.cart.repository.CartRepository;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jihun.myshop.global.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class CartValidator {

    private final ProductRepository productRepository;


    /**
     * 장바구니 상품 추가 요청 검증
     */
    public void validateAddCartItemRequest(CartItemCreateDto request) {
        // 1. 수량 유효성 검증
        validateQuantity(request.getQuantity());

        // 2. 상품 존재 여부 및 상태 검증
        Product product = validateProduct(request.getProductId());

        // 3. 재고 확인
        validateStock(product, request.getQuantity());
    }

    /**
     * 장바구니 상품 수량 변경 요청 검증
     */
    public void validateUpdateCartItemRequest(CartItemUpdateDto request) {
        // 수량 유효성 검증
        validateQuantity(request.getQuantity());
    }

    /**
     * 수량 유효성 검증
     */
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new CustomException(INVALID_INPUT_VALUE);
        }
    }

    /**
     * 상품 유효성 검증
     */
    private Product validateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

        if (product.getProductStatus() != ProductStatus.ACTIVE) {
            throw new CustomException(PRODUCT_NOT_AVAILABLE);
        }

        return product;
    }

    /**
     * 재고 유효성 검증
     */
    private void validateStock(Product product, int requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new CustomException(OUT_OF_STOCK);
        }
    }
}
