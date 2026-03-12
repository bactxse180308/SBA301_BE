package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.GlobalSearchResponse;

public interface SearchService {

    GlobalSearchResponse globalSearch(String keyword, int limit);
}
