package com.sba302.electroshop.specification;

import com.sba302.electroshop.entity.WishlistItem;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class WishlistItemSpecification {

    public static Specification<WishlistItem> hasWishlist(Integer wishlistId) {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("wishlist").get("wishlistId"), wishlistId);
    }

    public static Specification<WishlistItem> hasWishlistAndProduct(Integer wishlistId, Integer productId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("wishlist").get("wishlistId"), wishlistId));
            predicates.add(criteriaBuilder.equal(root.get("product").get("productId"), productId));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
