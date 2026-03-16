package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CompanyRequest;
import com.sba302.electroshop.dto.request.CreateCompanyRequest;
import com.sba302.electroshop.dto.response.CompanyResponse;
import com.sba302.electroshop.dto.response.CreateCompanyResponse;
import com.sba302.electroshop.enums.CompanyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    CompanyResponse getById(Integer id);

    Page<CompanyResponse> search(String keyword, Pageable pageable);

    CompanyResponse create(CompanyRequest request);

    CreateCompanyResponse createWithUser(CreateCompanyRequest request);

    CompanyResponse update(Integer id, CompanyRequest request);

    CompanyResponse updateStatus(Integer id, CompanyStatus status);

    CompanyResponse getByUserId(Integer userId);

    void delete(Integer id);
}
