package com.jihun.myshop.domain.user.entity.mapper;

import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.dto.AddressDto;
import org.mapstruct.Mapper;

import java.util.List;

import static com.jihun.myshop.domain.user.entity.dto.AddressDto.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse fromEntity(Address address);

    List<AddressResponse> fromEntityList(List<Address> addresses);
}