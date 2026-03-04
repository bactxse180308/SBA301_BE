package com.sba302.electroshop.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.sba302.electroshop.entity.Review;

import jakarta.persistence.criteria.Predicate;

public class ReviewSpecification {

    public static Specification<Review> filterReviews(Integer productId, Integer userId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (productId != null) {
                predicates.add(criteriaBuilder.equal(root.get("product").get("productId"), productId));
            }

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("userId"), userId));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<Review> hasUserAndProduct(Integer userId, Integer productId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("user").get("userId"), userId));
            predicates.add(criteriaBuilder.equal(root.get("product").get("productId"), productId));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
