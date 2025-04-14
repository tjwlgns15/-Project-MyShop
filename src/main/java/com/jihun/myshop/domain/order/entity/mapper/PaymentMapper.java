package com.jihun.myshop.domain.order.entity.mapper;

import com.jihun.myshop.domain.order.entity.Order;
import com.jihun.myshop.domain.order.entity.Payment;
import com.jihun.myshop.domain.order.entity.dto.OrderDto.OrderResponseDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto;
import com.jihun.myshop.domain.order.entity.dto.PaymentDto.PaymentResponseDto;
import com.jihun.myshop.domain.user.entity.mapper.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface PaymentMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "orderNumber", source = "order.orderNumber")
    @Mapping(target = "paymentMethodDescription", source = "paymentMethod.description")
    @Mapping(target = "paymentStatusDescription", source = "paymentStatus.description")
    PaymentResponseDto fromEntity(Payment payment);

}
