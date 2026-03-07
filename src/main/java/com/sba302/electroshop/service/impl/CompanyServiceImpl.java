package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CompanyRequest;
import com.sba302.electroshop.dto.response.CompanyResponse;
import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.exception.ResourceConflictException;
import com.sba302.electroshop.mapper.CompanyMapper;
import com.sba302.electroshop.repository.CompanyRepository;
import com.sba302.electroshop.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public CompanyResponse getById(Integer id) {
        log.info("Fetching company with id: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return companyMapper.toResponse(company);
    }

    @Override
    public Page<CompanyResponse> search(String keyword, Pageable pageable) {
        log.info("Searching companies with keyword: {}", keyword);
        Page<Company> companies;
        if (keyword != null && !keyword.trim().isEmpty()) {
            companies = companyRepository.findByCompanyNameContainingIgnoreCase(keyword, pageable);
        } else {
            companies = companyRepository.findAll(pageable);
        }
        return companies.map(companyMapper::toResponse);
    }

    @Override
    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        log.info("Creating new company: {}", request.getCompanyName());

        // Check duplicate tax code
        if (companyRepository.existsByTaxCode(request.getTaxCode())) {
            throw new ResourceConflictException("Company with tax code " + request.getTaxCode() + " already exists");
        }

        Company company = companyMapper.toEntity(request);
        Company savedCompany = companyRepository.save(company);
        log.info("Company created successfully with id: {}", savedCompany.getCompanyId());
        return companyMapper.toResponse(savedCompany);
    }

    @Override
    @Transactional
    public CompanyResponse update(Integer id, CompanyRequest request) {
        log.info("Updating company with id: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        // Check duplicate tax code (exclude current company)
        if (!company.getTaxCode().equals(request.getTaxCode()) &&
            companyRepository.existsByTaxCode(request.getTaxCode())) {
            throw new ResourceConflictException("Company with tax code " + request.getTaxCode() + " already exists");
        }

        companyMapper.updateEntity(company, request);
        Company updatedCompany = companyRepository.save(company);
        log.info("Company updated successfully with id: {}", id);
        return companyMapper.toResponse(updatedCompany);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting company with id: {}", id);
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found with id: " + id);
        }
        companyRepository.deleteById(id);
        log.info("Company deleted successfully with id: {}", id);
    }
}
