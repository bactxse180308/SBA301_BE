package com.sba302.electroshop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sba302.electroshop.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Double findAverageRatingByProductId(@Param("productId") Integer productId);

    @Query("SELECT r FROM Review r JOIN FETCH r.user JOIN FETCH r.product " +
           "WHERE LOWER(r.comment) LIKE :keyword " +
           "OR LOWER(r.product.productName) LIKE :keyword " +
           "OR LOWER(r.user.fullName) LIKE :keyword")
    List<Review> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
