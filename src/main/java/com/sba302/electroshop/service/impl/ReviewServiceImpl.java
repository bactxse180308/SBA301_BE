package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateReviewRequest;
import com.sba302.electroshop.dto.request.UpdateReviewRequest;
import com.sba302.electroshop.dto.response.ReviewResponse;
import com.sba302.electroshop.mapper.ReviewMapper;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.ReviewRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public ReviewResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public Page<ReviewResponse> search(Integer productId, Integer userId, Pageable pageable) {
        // TODO: Implement - search reviews with optional filters
        return null;
    }

    @Override
    public Double getAverageRating(Integer productId) {
        // TODO: Implement - calculate average rating for product
        return null;
    }

    @Override
    @Transactional
    public ReviewResponse create(Integer userId, CreateReviewRequest request) {
        // TODO: Implement - create new review
        return null;
    }

    @Override
    @Transactional
    public ReviewResponse update(Integer reviewId, UpdateReviewRequest request) {
        // TODO: Implement - update review
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete review
    }
}
