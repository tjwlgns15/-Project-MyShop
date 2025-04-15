package com.jihun.myshop.domain.cart.entity.mapper;

import com.jihun.myshop.domain.cart.entity.CartItem;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto.CartItemResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "mainImageUrl", source = "product.mainImageUrl")
    CartItemResponseDto fromEntity(CartItem cartItem);

    List<CartItemResponseDto> fromEntityList(List<CartItem> cartItems);
}
