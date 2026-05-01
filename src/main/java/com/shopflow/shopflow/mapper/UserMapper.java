package com.shopflow.shopflow.mapper;


import com.shopflow.shopflow.dto.response.UserResponse;
import com.shopflow.shopflow.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "nomBoutique", expression = "java(user.getSellerProfile() != null ? user.getSellerProfile().getNomBoutique() : null)")
    @Mapping(target = "descriptionBoutique", expression = "java(user.getSellerProfile() != null ? user.getSellerProfile().getDescription() : null)")
    @Mapping(target = "logoBoutique", expression = "java(user.getSellerProfile() != null ? user.getSellerProfile().getLogo() : null)")
    @Mapping(target = "noteBoutique", expression = "java(user.getSellerProfile() != null ? user.getSellerProfile().getNote() : null)")
    UserResponse toResponse(User user);
}
