package com.shopflow.shopflow.service;
import com.shopflow.shopflow.dto.request.ReviewRequest;
import com.shopflow.shopflow.dto.response.PageResponse;
import com.shopflow. shopflow.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, String customerEmail);
    PageResponse<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable);
    ReviewResponse approveReview(Long reviewId);
}
