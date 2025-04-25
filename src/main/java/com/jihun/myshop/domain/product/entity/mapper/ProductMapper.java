package com.jihun.myshop.domain.product.entity.mapper;

import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductImage;
import com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto.*;
import static com.jihun.myshop.domain.product.entity.dto.ProductWithImageDto.ProductResponseDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "sellerName", source = "product.seller.name")
    @Mapping(target = "sellerId", source = "product.seller.id")
    @Mapping(target = "mainImage", expression = "java(mapMainImage(product))")
    @Mapping(target = "additionalImages", expression = "java(mapAdditionalImages(product))")
    ProductResponseDto fromEntity(Product product);

    @Mapping(target = "productId", source = "product.id")
    ProductImageResponseDto toResponseDto(ProductImage image);

    default ProductImageResponseDto mapMainImage(Product product) {
        return product.getMainImage() != null ? toResponseDto(product.getMainImage()) : null;
    }

    default List<ProductImageResponseDto> mapAdditionalImages(Product product) {
        return product.getAdditionalImages().stream()
                .map(this::toResponseDto)
                .toList();
    }
}