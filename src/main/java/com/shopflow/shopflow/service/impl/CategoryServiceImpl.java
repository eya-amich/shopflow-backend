package com.shopflow.shopflow.service.impl;
import com.shopflow.shopflow. dto.request.CategoryRequest;
import com.shopflow.shopflow. dto.response.CategoryResponse;
import com.shopflow.shopflow. entity.Category;
import com.shopflow.shopflow. exception.DuplicateResourceException;
import com.shopflow.shopflow. exception.ResourceNotFoundException;
import com.shopflow.shopflow. mapper.CategoryMapper;
import com.shopflow.shopflow. repository.CategoryRepository;
import com.shopflow.shopflow. service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNom(request.getNom())) {
            throw new DuplicateResourceException("Une catégorie avec ce nom existe déjà");
        }

        Category category = Category.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .build();

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie parente non trouvée"));
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'id : " + id));

        category.setNom(request.getNom());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie parente non trouvée"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryResponse> getCategoryTree() {
        List<Category> rootCategories = categoryRepository.findByParentIsNull();
        return categoryMapper.toResponseList(rootCategories);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée avec l'id : " + id));
        return categoryMapper.toResponse(category);
    }
}
