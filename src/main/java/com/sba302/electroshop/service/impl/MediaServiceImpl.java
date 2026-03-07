package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateMediaRequest;
import com.sba302.electroshop.dto.response.MediaResponse;
import com.sba302.electroshop.entity.Media;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.MediaMapper;
import com.sba302.electroshop.repository.MediaRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.service.MediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final ProductRepository productRepository;
    private final MediaMapper mediaMapper;

    @Override
    public MediaResponse getById(Integer id) {
        log.info("Fetching media with id={}", id);
        return mediaRepository.findById(id)
                .map(mediaMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));
    }

    @Override
    public List<MediaResponse> getByProduct(Integer productId) {
        log.info("Fetching media for product id={}", productId);
        return mediaRepository.findByProduct_ProductIdOrderBySortOrderAsc(productId).stream()
                .map(mediaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MediaResponse> getAll(Pageable pageable) {
        log.info("Fetching all media");
        return mediaRepository.findAll(pageable)
                .map(mediaMapper::toResponse);
    }

    @Override
    @Transactional
    public MediaResponse create(CreateMediaRequest request) {
        log.info("Creating media for product id={}", request.getProductId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Media media = mediaMapper.toEntity(request);
        media.setProduct(product);

        media = mediaRepository.save(media);
        return mediaMapper.toResponse(media);
    }

    @Override
    @Transactional
    public MediaResponse update(Integer id, CreateMediaRequest request) {
        log.info("Updating media id={}", id);

        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));

        mediaMapper.updateEntity(media, request);

        if (!media.getProduct().getProductId().equals(request.getProductId())) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
            media.setProduct(product);
        }

        media = mediaRepository.save(media);
        return mediaMapper.toResponse(media);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting media id={}", id);
        if (!mediaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Media not found with id: " + id);
        }
        mediaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateSortOrder(Integer id, Integer sortOrder) {
        log.info("Updating sort order for media id={}", id);
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));

        media.setSortOrder(sortOrder);
        mediaRepository.save(media);
    }
}