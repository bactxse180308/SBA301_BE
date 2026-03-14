package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.BannerCreateRequest;
import com.sba302.electroshop.dto.request.BannerUpdateRequest;
import com.sba302.electroshop.dto.response.BannerResponse;
import com.sba302.electroshop.dto.response.HomeBannerResponse;
import com.sba302.electroshop.entity.Banner;
import com.sba302.electroshop.enums.BannerPosition;
import com.sba302.electroshop.exception.BannerNotFoundException;
import com.sba302.electroshop.repository.BannerRepository;
import com.sba302.electroshop.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    private static final Pattern BANNER_KEY_PATTERN = Pattern.compile("^banners/.*");
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?://)?(/[\\w.-]*)*$", Pattern.CASE_INSENSITIVE);

    @Override
    @Transactional
    public BannerResponse createBanner(BannerCreateRequest request) {
        log.info("Creating banner with position={}, title={}", request.getPosition(), request.getTitle());

        validateBannerRequest(request);

        Banner banner = Banner.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .imageKey(request.getImageKey())
                .buttonText(request.getButtonText())
                .buttonLink(request.getButtonLink())
                .position(request.getPosition())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .backgroundColor(request.getBackgroundColor())
                .textColor(request.getTextColor())
                .build();

        Banner saved = bannerRepository.save(banner);
        log.info("Created banner with id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public BannerResponse updateBanner(Long id, BannerUpdateRequest request) {
        log.info("Updating banner id={}", id);

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new BannerNotFoundException("Không tìm thấy banner: " + id));

        if (request.getTitle() != null) {
            banner.setTitle(request.getTitle());
        }
        if (request.getSubtitle() != null) {
            banner.setSubtitle(request.getSubtitle());
        }
        if (request.getDescription() != null) {
            banner.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            banner.setImageUrl(request.getImageUrl());
        }
        if (request.getImageKey() != null) {
            banner.setImageKey(request.getImageKey());
        }
        if (request.getButtonText() != null) {
            banner.setButtonText(request.getButtonText());
        }
        if (request.getButtonLink() != null) {
            banner.setButtonLink(request.getButtonLink());
        }
        if (request.getPosition() != null) {
            banner.setPosition(request.getPosition());
        }
        if (request.getSortOrder() != null) {
            banner.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            banner.setIsActive(request.getIsActive());
        }
        if (request.getStartDate() != null) {
            banner.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            banner.setEndDate(request.getEndDate());
        }
        if (request.getBackgroundColor() != null) {
            banner.setBackgroundColor(request.getBackgroundColor());
        }
        if (request.getTextColor() != null) {
            banner.setTextColor(request.getTextColor());
        }

        Banner saved = bannerRepository.save(banner);
        log.info("Updated banner id={}", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        log.info("Deleting banner id={}", id);

        if (!bannerRepository.existsById(id)) {
            throw new BannerNotFoundException("Không tìm thấy banner: " + id);
        }

        bannerRepository.deleteById(id);
        log.info("Deleted banner id={}", id);
    }

    @Override
    public BannerResponse getBannerById(Long id) {
        return bannerRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new BannerNotFoundException("Không tìm thấy banner: " + id));
    }

    @Override
    public Page<BannerResponse> getAllBanners(BannerPosition position, Boolean isActive, String keyword, Pageable pageable) {
        return bannerRepository.findByFilters(position, isActive, keyword, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public BannerResponse updateBannerStatus(Long id, Boolean isActive) {
        log.info("Updating banner id={} status to isActive={}", id, isActive);

        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new BannerNotFoundException("Không tìm thấy banner: " + id));

        banner.setIsActive(isActive);
        Banner saved = bannerRepository.save(banner);

        log.info("Updated banner id={} isActive={}", id, isActive);
        return toResponse(saved);
    }

    @Override
    public HomeBannerResponse getHomeBanners() {
        List<Banner> mainBanners = bannerRepository.findActiveAndValidByPosition(BannerPosition.MAIN);
        List<Banner> rightTopBanners = bannerRepository.findActiveAndValidByPosition(BannerPosition.RIGHT_TOP);
        List<Banner> rightBottomBanners = bannerRepository.findActiveAndValidByPosition(BannerPosition.RIGHT_BOTTOM);

        return HomeBannerResponse.builder()
                .main(mainBanners.stream().map(this::toResponse).toList())
                .rightTop(rightTopBanners.stream().map(this::toResponse).toList())
                .rightBottom(rightBottomBanners.stream().map(this::toResponse).toList())
                .build();
    }

    private void validateBannerRequest(BannerCreateRequest request) {
        if (request.getImageKey() != null && !request.getImageKey().isEmpty()) {
            if (!BANNER_KEY_PATTERN.matcher(request.getImageKey()).matches()) {
                throw new IllegalArgumentException("Image key phải bắt đầu bằng 'banners/'");
            }
        }

        if (request.getButtonLink() != null && !request.getButtonLink().isEmpty()) {
            if (!URL_PATTERN.matcher(request.getButtonLink()).matches()) {
                throw new IllegalArgumentException("Đường dẫn button không hợp lệ");
            }
        }
    }

    private BannerResponse toResponse(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .subtitle(banner.getSubtitle())
                .description(banner.getDescription())
                .imageUrl(banner.getImageUrl())
                .imageKey(banner.getImageKey())
                .buttonText(banner.getButtonText())
                .buttonLink(banner.getButtonLink())
                .position(banner.getPosition())
                .sortOrder(banner.getSortOrder())
                .isActive(banner.getIsActive())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .backgroundColor(banner.getBackgroundColor())
                .textColor(banner.getTextColor())
                .createdAt(banner.getCreatedAt())
                .updatedAt(banner.getUpdatedAt())
                .build();
    }
}
