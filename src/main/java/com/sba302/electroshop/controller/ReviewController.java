package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.AdminReplyRequest;
import com.sba302.electroshop.dto.request.CreateReviewRequest;
import com.sba302.electroshop.dto.request.UpdateReviewRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.ProductRatingStatsResponse;
import com.sba302.electroshop.dto.response.ReviewResponse;
import com.sba302.electroshop.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // Các field hợp lệ để sort
    private static final java.util.Set<String> VALID_SORT_FIELDS =
            java.util.Set.of("reviewId", "rating", "reviewDate", "replyDate");

    @GetMapping
    public ApiResponse<Page<ReviewResponse>> search(
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        // Validate sortBy để tránh injection và lỗi JPA sort
        String safeSortBy = VALID_SORT_FIELDS.contains(sortBy) ? sortBy : "reviewDate";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, safeSortBy));
        return ApiResponse.success(reviewService.search(productId, userId, pageable));
    }

    @GetMapping("/product/{productId}/rating")
    public ApiResponse<Double> getAverageRating(@PathVariable Integer productId) {
        return ApiResponse.success(reviewService.getAverageRating(productId));
    }

    @GetMapping("/product/{productId}/rating-stats")
    public ApiResponse<ProductRatingStatsResponse> getProductRatingStats(@PathVariable Integer productId) {
        return ApiResponse.success(reviewService.getProductRatingStats(productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and authentication.principal.toString() == #userId.toString()")
    public ApiResponse<ReviewResponse> create(
            @RequestParam Integer userId,
            @Valid @RequestBody CreateReviewRequest request) {
        return ApiResponse.success(reviewService.create(userId, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReviewResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateReviewRequest request) {
        return ApiResponse.success(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        reviewService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ReviewResponse> adminReply(
            @PathVariable Integer id,
            @RequestParam Integer adminUserId,
            @Valid @RequestBody AdminReplyRequest request) {
        return ApiResponse.success(reviewService.adminReply(id, adminUserId, request));
    }
}
