package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateReviewRequest;
import com.sba302.electroshop.dto.request.UpdateReviewRequest;
import com.sba302.electroshop.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponse getById(Integer id);

    Page<ReviewResponse> search(Integer productId, Integer userId, Pageable pageable);

    Double getAverageRating(Integer productId);

    ReviewResponse create(Integer userId, CreateReviewRequest request);

    ReviewResponse update(Integer reviewId, UpdateReviewRequest request);

    void delete(Integer id);
}
