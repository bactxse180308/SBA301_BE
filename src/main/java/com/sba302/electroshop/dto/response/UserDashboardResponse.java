package com.sba302.electroshop.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class UserDashboardResponse {
    private UserResponse userInfo;
    private Long totalOrders;
    private BigDecimal totalSpent;
    private List<RecentOrderResponse> recentOrders;
    private List<ReviewResponse> recentReviews;
    private List<WishlistResponse.WishlistItemResponse> wishlist;
}
