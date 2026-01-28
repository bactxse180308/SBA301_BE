package com.sba302.electroshop.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Integer reviewId;
    private Integer userId;
    private String userFullName;
    private Integer productId;
    private String productName;
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
}
