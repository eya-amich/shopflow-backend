package com.shopflow.shopflow.mapper;
import com.shopflow. shopflow.dto.response.ReviewResponse;
import com.shopflow. shopflow.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productNom", source = "product.nom")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerNom", expression = "java(review.getCustomer().getPrenom() + \" \" + review.getCustomer().getNom())")
    ReviewResponse toResponse(Review review);

    List<ReviewResponse> toResponseList(List<Review> reviews);
}
