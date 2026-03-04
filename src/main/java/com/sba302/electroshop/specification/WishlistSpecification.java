package com.sba302.electroshop.specification;

import com.sba302.electroshop.entity.Wishlist;
import org.springframework.data.jpa.domain.Specification;

public class WishlistSpecification {

    public static Specification<Wishlist> hasUser(Integer userId) {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("user").get("userId"), userId);
    }
}
