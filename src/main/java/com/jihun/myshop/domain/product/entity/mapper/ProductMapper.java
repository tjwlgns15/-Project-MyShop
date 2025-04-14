package com.jihun.myshop.domain.product.entity.mapper;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import com.jihun.myshop.domain.product.entity.dto.ProductDto.ProductResponse;
import com.jihun.myshop.domain.user.entity.User;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import static com.jihun.myshop.domain.product.entity.dto.ProductDto.ProductCreateDto;
import static com.jihun.myshop.domain.product.entity.dto.ProductDto.ProductUpdateDto;

@Mapper(componentModel = "spring", imports = {ArrayList.class, ProductStatus.class})
public interface ProductMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "sellerName", source = "seller.name")
    @Mapping(target = "sellerId", source = "seller.id")
    ProductResponse fromEntity(Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "seller", source = "user")
    @Mapping(target = "name", source = "productCreateDto.name")
    @Mapping(target = "description", source = "productCreateDto.description")
    @Mapping(target = "price", source = "productCreateDto.price")
    @Mapping(target = "reviews", expression = "java(new ArrayList<>())")
    @Mapping(target = "productStatus", expression = "java(ProductStatus.ACTIVE)")
    @Mapping(target = "discountType", source = "productCreateDto.discountType")
    @Mapping(target = "discountValue", source = "productCreateDto.discountValue")
    @Mapping(target = "discountPrice", source = "discountPrice")
    Product fromCreateDto(ProductCreateDto productCreateDto, Category category, User user, BigDecimal discountPrice);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ProductUpdateDto productUpdateDto, @MappingTarget Product entity);
}
