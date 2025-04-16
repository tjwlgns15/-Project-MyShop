package com.jihun.myshop.domain.cart.validator;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.cart.entity.CartItem;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemCreateDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemUpdateDto;
import com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto;
import com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto.CartOrderDto;
import com.jihun.myshop.domain.cart.entity.dto.CartToOrderDto.CartOrderSelectDto;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import com.jihun.myshop.domain.product.repository.ProductRepository;
import com.jihun.myshop.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.jihun.myshop.global.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class CartToOrderValidator {

    public void checkStockQuantity(int stockQuantity, int compareQuantity) {
        if (stockQuantity < compareQuantity) {
            throw new CustomException(OUT_OF_STOCK);
        }
    }


    public void validateIsEmptyCart(Cart cart) {
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new CustomException(CART_IS_EMPTY);
        }
    }

    public void validateCartItemIds(List<Long> cartItemIds) {
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            throw new CustomException(CART_ITEMS_NOT_SELECTED);
        }
    }
}
