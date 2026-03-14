package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.BannerCreateRequest;
import com.sba302.electroshop.dto.request.BannerUpdateRequest;
import com.sba302.electroshop.dto.response.BannerResponse;
import com.sba302.electroshop.dto.response.HomeBannerResponse;
import com.sba302.electroshop.enums.BannerPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BannerService {

    BannerResponse createBanner(BannerCreateRequest request);

    BannerResponse updateBanner(Long id, BannerUpdateRequest request);

    void deleteBanner(Long id);

    BannerResponse getBannerById(Long id);

    Page<BannerResponse> getAllBanners(BannerPosition position, Boolean isActive, String keyword, Pageable pageable);

    BannerResponse updateBannerStatus(Long id, Boolean isActive);

    HomeBannerResponse getHomeBanners();
}
