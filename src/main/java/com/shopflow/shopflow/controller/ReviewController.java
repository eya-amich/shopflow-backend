package com.shopflow.shopflow.controller;

import com.shopflow.shopflow.dto.request.ReviewRequest;
import com.shopflow.shopflow.dto.response.PageResponse;
import com.shopflow.shopflow.dto.response.ReviewResponse;
import com.shopflow.shopflow.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        return new ResponseEntity<>(reviewService.createReview(request, authentication.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<PageResponse<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId, pageable));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ReviewResponse> approveReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }
}