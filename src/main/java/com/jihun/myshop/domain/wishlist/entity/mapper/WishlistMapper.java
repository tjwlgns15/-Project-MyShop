package com.jihun.myshop.domain.wishlist.entity.mapper;

import com.jihun.myshop.domain.wishlist.entity.WishlistItem;
import com.jihun.myshop.domain.wishlist.entity.dto.WishlistItemDto;
import com.jihun.myshop.domain.wishlist.entity.dto.WishlistItemDto.WishlistResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.mainImageUrl")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "discountPrice", source = "product.discountPrice")
    WishlistResponseDto fromEntity(WishlistItem wishlistItem);
}
