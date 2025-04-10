package com.jihun.myshop.domain.product.entity.mapper;

import com.jihun.myshop.domain.product.entity.Category;
import com.jihun.myshop.domain.product.entity.DiscountType;
import com.jihun.myshop.domain.product.entity.Product;
import com.jihun.myshop.domain.product.entity.ProductStatus;
import com.jihun.myshop.domain.product.entity.dto.ProductDto.ProductResponse;
import com.jihun.myshop.domain.user.entity.User;
import org.mapstruct.*;

import java.util.ArrayList;

import static com.jihun.myshop.domain.product.entity.DiscountType.FIXED_AMOUNT;
import static com.jihun.myshop.domain.product.entity.DiscountType.PERCENTAGE;
import static com.jihun.myshop.domain.product.entity.dto.ProductDto.ProductCreate;
import static com.jihun.myshop.domain.product.entity.dto.ProductDto.ProductUpdate;

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
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "price", source = "dto.price")
    @Mapping(target = "reviews", expression = "java(new ArrayList<>())")
    @Mapping(target = "productStatus", expression = "java(ProductStatus.ACTIVE)")
    @Mapping(target = "discountType", source = "dto.discountType")
    @Mapping(target = "discountValue", source = "dto.discountValue")
    @Mapping(target = "discountPrice", source = "discountPrice")
    Product fromCreateDto(ProductCreate dto, Category category, User user, Long discountPrice);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(ProductUpdate dto, @MappingTarget Product entity);
}
