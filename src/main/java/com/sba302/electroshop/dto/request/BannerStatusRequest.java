package com.sba302.electroshop.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerStatusRequest {
    private Boolean isActive;
}
