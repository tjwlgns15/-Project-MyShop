package com.jihun.myshop.domain.product.entity.mapper;

import com.jihun.myshop.domain.product.entity.Review;
import com.jihun.myshop.domain.product.entity.dto.ReviewDto;
import com.jihun.myshop.domain.product.entity.dto.ReviewDto.ReviewResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static com.jihun.myshop.domain.product.entity.dto.ReviewDto.*;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    ReviewResponseDto fromEntity(Review review);

    SummaryDto toSummaryDto(Long productId, int totalReviews, double averageRating);



}
