package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.Review;
import com.sba302.electroshop.repository.ReviewRepository;
import com.sba302.electroshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> findById(Integer id) {
        return reviewRepository.findById(id);
    }

    @Override
    @Transactional
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        reviewRepository.deleteById(id);
    }
}
