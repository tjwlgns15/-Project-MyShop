package com.jihun.myshop.domain.cart.entity.mapper;

import com.jihun.myshop.domain.cart.entity.Cart;
import com.jihun.myshop.domain.cart.entity.dto.CartDto;
import com.jihun.myshop.domain.cart.entity.dto.CartDto.CartResponseDto;
import com.jihun.myshop.domain.cart.entity.dto.CartItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    @Mapping(target = "totalItems", expression = "java(cart.getItems() != null ? cart.getItems().size() : 0)")
    CartResponseDto fromEntity(Cart cart);

    List<CartResponseDto> fromEntityList(List<Cart> carts);
}
