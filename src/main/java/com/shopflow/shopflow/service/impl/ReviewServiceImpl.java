package com.shopflow.shopflow.service.impl;
import com.shopflow.shopflow. dto.request.ReviewRequest;
import com.shopflow.shopflow. dto.response.PageResponse;
import com.shopflow.shopflow. dto.response.ReviewResponse;
import com.shopflow.shopflow. entity.Product;
import com.shopflow.shopflow. entity.Review;
import com.shopflow.shopflow. entity.User;
import com.shopflow.shopflow. exception.BadRequestException;
import com.shopflow.shopflow. exception.DuplicateResourceException;
import com.shopflow.shopflow. exception.ResourceNotFoundException;
import com.shopflow.shopflow. mapper.ReviewMapper;
import com.shopflow.shopflow. repository.OrderRepository;
import com.shopflow.shopflow. repository.ProductRepository;
import com.shopflow.shopflow. repository.ReviewRepository;
import com.shopflow.shopflow. repository.UserRepository;
import com.shopflow.shopflow. service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé"));

        // Vérifier achat
        boolean hasPurchased = orderRepository.hasCustomerPurchasedProduct(customer.getId(), product.getId());
        if (!hasPurchased) {
            throw new BadRequestException("Vous devez avoir acheté ce produit pour laisser un avis");
        }

        // Vérifier doublon
        if (reviewRepository.existsByCustomerIdAndProductId(customer.getId(), product.getId())) {
            throw new DuplicateResourceException("Vous avez déjà laissé un avis sur ce produit");
        }

        Review review = Review.builder()
                .customer(customer)
                .product(product)
                .note(request.getNote())
                .commentaire(request.getCommentaire())
                .approuve(false)
                .build();

        review = reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }

    @Override
    public PageResponse<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByProductIdAndApprouveTrue(productId, pageable);

        List<ReviewResponse> content = page.getContent().stream()
                .map(reviewMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<ReviewResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Avis non trouvé"));
        review.setApprouve(true);
        review = reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }
}
