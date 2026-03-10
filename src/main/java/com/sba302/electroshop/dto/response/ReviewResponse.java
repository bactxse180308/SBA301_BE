package com.sba302.electroshop.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String replyComment;
    private LocalDateTime replyDate;
    private Integer repliedByUserId;
    private String repliedByFullName;
}
