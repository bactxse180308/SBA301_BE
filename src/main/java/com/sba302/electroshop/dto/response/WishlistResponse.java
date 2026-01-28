package com.sba302.electroshop.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private Integer wishlistId;
    private Integer userId;
    private LocalDateTime createdDate;
    private List<WishlistItemResponse> items;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WishlistItemResponse {
        private Integer productId;
        private String productName;
        private String productImageUrl;
        private LocalDateTime createdDate;
    }
}
