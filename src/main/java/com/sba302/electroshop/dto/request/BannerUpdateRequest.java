package com.sba302.electroshop.dto.request;

import com.sba302.electroshop.enums.BannerPosition;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerUpdateRequest {

    private String title;

    private String subtitle;

    private String description;

    private String imageUrl;

    private String imageKey;

    private String buttonText;

    private String buttonLink;

    private BannerPosition position;

    @PositiveOrZero(message = "Thứ tự hiển thị không được âm")
    private Integer sortOrder;

    private Boolean isActive;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String backgroundColor;

    private String textColor;
}
