package com.jihun.myshop.domain.order.entity.mapper;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderResponseDto;
import com.jihun.myshop.domain.user.entity.mapper.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class, AddressMapper.class})
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "orderStatusDescription", source = "orderStatus.description")
    OrderResponseDto fromEntity(Order order);

    List<OrderResponseDto> fromEntityList(List<Order> orders);
}
