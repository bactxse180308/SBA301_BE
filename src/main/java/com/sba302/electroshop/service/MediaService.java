package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateMediaRequest;
import com.sba302.electroshop.dto.response.MediaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MediaService {

    MediaResponse getById(Integer id);

    List<MediaResponse> getByProduct(Integer productId);

    Page<MediaResponse> getAll(Pageable pageable);

    MediaResponse create(CreateMediaRequest request);

    MediaResponse update(Integer id, CreateMediaRequest request);

    void delete(Integer id);

    void updateSortOrder(Integer id, Integer sortOrder);
}