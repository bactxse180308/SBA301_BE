package com.sba302.electroshop.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sba302.electroshop.dto.request.AdminReplyRequest;
import com.sba302.electroshop.dto.request.CreateReviewRequest;
import com.sba302.electroshop.dto.request.UpdateReviewRequest;
import com.sba302.electroshop.dto.response.ProductRatingStatsResponse;
import com.sba302.electroshop.dto.response.ReviewResponse;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.entity.Review;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.enums.OrderStatus;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceConflictException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.ReviewMapper;
import com.sba302.electroshop.repository.OrderRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.ReviewRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.ReviewService;
import com.sba302.electroshop.specification.ReviewSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse getById(Integer id) {
        log.info("Getting review by id: {}", id);
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        
        return reviewMapper.toResponse(review);
    }

    @Override
    public Page<ReviewResponse> search(Integer productId, Integer userId, Pageable pageable) {
        log.info("Searching reviews with productId: {}, userId: {}", productId, userId);
        
        Page<Review> reviews = reviewRepository.findAll(
                ReviewSpecification.filterReviews(productId, userId), pageable);
        
        return reviews.map(reviewMapper::toResponse);
    }

    @Override
    public Double getAverageRating(Integer productId) {
        log.info("Getting average rating for product: {}", productId);
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
        
        Double avgRating = reviewRepository.findAverageRatingByProductId(productId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Override
    public ProductRatingStatsResponse getProductRatingStats(Integer productId) {
        log.info("Getting rating stats for product: {}", productId);

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        Double avgRating = reviewRepository.findAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.countByProductId(productId);

        return ProductRatingStatsResponse.builder()
                .productId(productId)
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0)
                .totalReviews(totalReviews)
                .fiveStar(reviewRepository.countByProductIdAndRating(productId, 5))
                .fourStar(reviewRepository.countByProductIdAndRating(productId, 4))
                .threeStar(reviewRepository.countByProductIdAndRating(productId, 3))
                .twoStar(reviewRepository.countByProductIdAndRating(productId, 2))
                .oneStar(reviewRepository.countByProductIdAndRating(productId, 1))
                .build();
    }

    @Override
    @Transactional
    public ReviewResponse create(Integer userId, CreateReviewRequest request) {
        log.info("Creating review for product: {} by user: {}", request.getProductId(), userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
        
        // Validate: User must have purchased the product
        if (!orderRepository.hasUserPurchasedProduct(userId, request.getProductId(), OrderStatus.DELIVERED)) {
            throw new ApiException("You can only review products that you have purchased and received");
        }
        
        // Check if user already reviewed this product
        boolean alreadyReviewed = reviewRepository.exists(
                ReviewSpecification.hasUserAndProduct(userId, request.getProductId()));
        
        if (alreadyReviewed) {
            throw new ResourceConflictException("You have already reviewed this product");
        }
        
        Review review = reviewMapper.toEntity(request);
        review.setUser(user);
        review.setProduct(product);
        review.setReviewDate(LocalDateTime.now());
        
        Review savedReview = reviewRepository.save(review);
        
        // Update product average rating
        Double avgRating = reviewRepository.findAverageRatingByProductId(request.getProductId());
        product.setRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        productRepository.save(product);

        log.info("Review created successfully with id: {}", savedReview.getReviewId());
        
        return reviewMapper.toResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse update(Integer reviewId, UpdateReviewRequest request) {
        log.info("Updating review: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        reviewMapper.updateEntity(review, request);
        
        Review updatedReview = reviewRepository.save(review);
        
        log.info("Review updated successfully: {}", reviewId);
        
        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting review: {}", id);
        
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        
        reviewRepository.deleteById(id);
        
        log.info("Review deleted successfully: {}", id);
    }

    @Override
    @Transactional
    public ReviewResponse adminReply(Integer reviewId, Integer adminUserId, AdminReplyRequest request) {
        log.info("Admin {} replying to review: {}", adminUserId, reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + adminUserId));

        review.setReplyComment(request.getReplyComment());
        review.setReplyDate(LocalDateTime.now());
        review.setRepliedBy(admin);

        Review savedReview = reviewRepository.save(review);

        log.info("Admin reply added to review: {}", reviewId);

        return reviewMapper.toResponse(savedReview);
    }
}
