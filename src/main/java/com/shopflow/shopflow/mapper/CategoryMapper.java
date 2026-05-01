package com.shopflow.shopflow.mapper;
import com.shopflow.shopflow.dto.response.CategoryResponse;
import com.shopflow. shopflow.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentId", expression = "java(category.getParent() != null ? category.getParent().getId() : null)")
    @Mapping(target = "parentNom", expression = "java(category.getParent() != null ? category.getParent().getNom() : null)")
    @Mapping(target = "sousCategories", source = "sousCategories")
    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
