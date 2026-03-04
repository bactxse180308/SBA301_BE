package com.sba302.electroshop.specification;

import com.sba302.electroshop.entity.Order;
import com.sba302.electroshop.enums.OrderStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> hasUserAndProductDelivered(Integer userId, Integer productId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join with OrderDetail
            Join<Object, Object> orderDetail = root.join("orderDetails");

            // User condition
            predicates.add(criteriaBuilder.equal(root.get("user").get("userId"), userId));

            // Product condition from OrderDetail
            predicates.add(criteriaBuilder.equal(orderDetail.get("product").get("productId"), productId));

            // Order status must be DELIVERED
            predicates.add(criteriaBuilder.equal(root.get("orderStatus"), OrderStatus.DELIVERED));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
