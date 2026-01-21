package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> findAll();

    Optional<Review> findById(Integer id);

    Review save(Review review);

    void deleteById(Integer id);
}
