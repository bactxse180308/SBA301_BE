package com.sba302.electroshop.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeBannerResponse {
    private List<BannerResponse> main;
    private List<BannerResponse> rightTop;
    private List<BannerResponse> rightBottom;
}
