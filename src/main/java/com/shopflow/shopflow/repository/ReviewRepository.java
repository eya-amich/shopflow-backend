package com.shopflow.shopflow.repository;

import com.shopflow.shopflow.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Page<Review> findByProductIdAndApprouveTrue(Long productId, Pageable pageable);

    List<Review> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);

    long countByProductIdAndApprouveTrue(Long productId);

    @Query("SELECT COALESCE(AVG(r.note), 0) FROM Review r WHERE r.product.id = :productId AND r.approuve = true")
    Double calculateAverageRating(@Param("productId") Long productId);
}