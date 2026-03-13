package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.PublicSearchResponse;

public interface PublicSearchService {
    PublicSearchResponse search(String keyword, int limit);
}
