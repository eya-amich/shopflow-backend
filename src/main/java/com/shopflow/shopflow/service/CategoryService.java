package com.shopflow.shopflow.service;
import com.shopflow.shopflow.dto.request.CategoryRequest;
import com.shopflow. shopflow.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    List<CategoryResponse> getCategoryTree();
    CategoryResponse getCategoryById(Long id);
}
