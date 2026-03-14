package com.sba302.electroshop.dto.response;

import com.sba302.electroshop.enums.BannerPosition;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponse {
    private Long id;
    private String title;
    private String subtitle;
    private String description;
    private String imageUrl;
    private String imageKey;
    private String buttonText;
    private String buttonLink;
    private BannerPosition position;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String backgroundColor;
    private String textColor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
