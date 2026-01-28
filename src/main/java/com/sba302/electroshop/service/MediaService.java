package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateMediaRequest;
import com.sba302.electroshop.dto.response.MediaResponse;
import java.util.List;

public interface MediaService {

    MediaResponse getById(Integer id);

    List<MediaResponse> getByProduct(Integer productId);

    MediaResponse create(CreateMediaRequest request);

    MediaResponse update(Integer id, CreateMediaRequest request);

    void delete(Integer id);

    void updateSortOrder(Integer id, Integer sortOrder);
}
