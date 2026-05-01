package com.shopflow.shopflow.repository;



import com.shopflow.shopflow.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentIsNull(); // catégories racines

    Optional<Category> findByNom(String nom);

    boolean existsByNom(String nom);
}
