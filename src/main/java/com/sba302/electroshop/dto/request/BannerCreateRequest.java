package com.sba302.electroshop.dto.request;

import com.sba302.electroshop.enums.BannerPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerCreateRequest {

    private String title;

    private String subtitle;

    private String description;

    @NotBlank(message = "Ảnh banner không được để trống")
    private String imageUrl;

    private String imageKey;

    private String buttonText;

    private String buttonLink;

    @NotNull(message = "Vị trí banner không được để trống")
    private BannerPosition position;

    @PositiveOrZero(message = "Thứ tự hiển thị không được âm")
    @Builder.Default
    private Integer sortOrder = 0;

    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String backgroundColor;

    private String textColor;
}
