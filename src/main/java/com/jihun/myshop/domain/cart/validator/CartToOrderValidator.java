package com.jihun.myshop.domain.cart.validator;

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

}
