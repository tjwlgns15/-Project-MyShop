package com.jihun.myshop.domain.order.entity.mapper;

import com.jihun.myshop.domain.order.entity.OrderItem;
import com.jihun.myshop.domain.product.entity.mapper.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static com.jihun.myshop.domain.order.entity.dto.OrderItemDto.*;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface OrderItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.mainImageUrl")
    OrderItemResponseDto fromEntity(OrderItem orderItem);

    List<OrderItemResponseDto> fromEntityList(List<OrderItem> orderItems);
}
