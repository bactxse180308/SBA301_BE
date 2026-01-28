package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateReviewRequest;
import com.sba302.electroshop.dto.request.UpdateReviewRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.ReviewResponse;
import com.sba302.electroshop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public ApiResponse<ReviewResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(reviewService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<ReviewResponse>> search(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer userId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(reviewService.search(productId, userId, pageable));
    }

    @GetMapping("/product/{productId}/rating")
    public ApiResponse<Double> getAverageRating(@PathVariable Integer productId) {
        return ApiResponse.success(reviewService.getAverageRating(productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResponse> create(
            @RequestParam Integer userId,
            @Valid @RequestBody CreateReviewRequest request) {
        return ApiResponse.success(reviewService.create(userId, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ReviewResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateReviewRequest request) {
        return ApiResponse.success(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        reviewService.delete(id);
        return ApiResponse.success(null);
    }
}
