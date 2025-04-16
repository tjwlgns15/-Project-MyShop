package com.jihun.myshop.domain.user.entity.mapper;

import com.jihun.myshop.domain.user.entity.Address;
import com.jihun.myshop.domain.user.entity.Role;
import com.jihun.myshop.domain.user.entity.User;
import com.jihun.myshop.domain.user.entity.dto.AddressDto.AddressResponseDto;
import org.mapstruct.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static com.jihun.myshop.domain.user.entity.dto.UserDto.*;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface UserMapper {

    @Mapping(target = "username", source = "createDto.username")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(createDto.getPassword()))")
    @Mapping(target = "name", source = "createDto.name")
    @Mapping(target = "phone", source = "createDto.phone")
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "wishlistItems", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    User fromCreateDto(UserCreateDto createDto, @Context PasswordEncoder passwordEncoder);

    @Mapping(target = "password", constant = "null")
    @Mapping(target = "userRoles", expression = "java(mapRoles(user.getUserRoles()))")
    @Mapping(target = "defaultAddress", expression = "java(findDefaultAddress(user))")
    UserResponseDto fromEntity(User user);

    @Mapping(target = "userRoles", expression = "java(mapRoles(user.getUserRoles()))")
    @Mapping(target = "defaultAddress", expression = "java(findDefaultAddress(user))")
    UserResponseDto fromEntityIncludePw(User user);


    // 헬퍼 메서드 인터페이스 선언
    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getRoleName)
                .collect(java.util.stream.Collectors.toSet());
    }

    default AddressResponseDto findDefaultAddress(User user) {
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            return null;
        }

        return user.getAddresses().stream()
                .filter(Address::isDefault)
                .findFirst()
                .map(this::addressToDto)
                .orElse(null);
    }

    default AddressResponseDto addressToDto(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponseDto.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .zipCode(address.getZipCode())
                .address1(address.getAddress1())
                .address2(address.getAddress2())
                .phone(address.getPhone())
                .isDefault(address.isDefault())
                .build();
    }

//    default User updateUserFromDto(UserSignupDto dto, User user, @Context PasswordEncoder passwordEncoder) {
//        if (dto == null) {
//            return user;
//        }
//
//        User.UserBuilder builder = User.builder()
//                .id(user.getId())
//                .username(user.getUsername())
//                .password(user.getPassword())
//                .name(user.getName())
//                .phone(user.getPhone())
//                .userRoles(user.getUserRoles())
//                .addresses(user.getAddresses())
//                .orders(user.getOrders())
//                .cart(user.getCart())
//                .reviews(user.getReviews())
//                .wishlistItems(user.getWishlistItems());
//
//        if (dto.getName() != null) {
//            builder.name(dto.getName());
//        }
//        if (dto.getPhone() != null) {
//            builder.phone(dto.getPhone());
//        }
//        if (dto.getPassword() != null) {
//            builder.password(passwordEncoder.encode(dto.getPassword()));
//        }
//
//        return builder.build();
//    }
}