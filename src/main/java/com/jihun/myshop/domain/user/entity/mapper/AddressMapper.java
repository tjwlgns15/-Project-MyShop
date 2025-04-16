package com.jihun.myshop.domain.user.entity.mapper;

import com.jihun.myshop.domain.user.entity.Address;
import org.mapstruct.Mapper;

import java.util.List;

import static com.jihun.myshop.domain.user.entity.dto.AddressDto.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponseDto fromEntity(Address address);

    List<AddressResponseDto> fromEntityList(List<Address> addresses);
}