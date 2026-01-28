package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateMediaRequest;
import com.sba302.electroshop.dto.response.MediaResponse;
import com.sba302.electroshop.mapper.MediaMapper;
import com.sba302.electroshop.repository.MediaRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final ProductRepository productRepository;
    private final MediaMapper mediaMapper;

    @Override
    public MediaResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public List<MediaResponse> getByProduct(Integer productId) {
        // TODO: Implement - get all media for product
        return null;
    }

    @Override
    @Transactional
    public MediaResponse create(CreateMediaRequest request) {
        // TODO: Implement - create media
        return null;
    }

    @Override
    @Transactional
    public MediaResponse update(Integer id, CreateMediaRequest request) {
        // TODO: Implement - update media
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete media
    }

    @Override
    @Transactional
    public void updateSortOrder(Integer id, Integer sortOrder) {
        // TODO: Implement - update media sort order
    }
}
