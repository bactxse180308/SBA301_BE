package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CompanyRequest;
import com.sba302.electroshop.dto.request.CreateCompanyRequest;
import com.sba302.electroshop.dto.response.CompanyResponse;
import com.sba302.electroshop.dto.response.CreateCompanyResponse;
import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.entity.Role;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.enums.CompanyStatus;
import com.sba302.electroshop.exception.ResourceConflictException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.CompanyMapper;
import com.sba302.electroshop.repository.CompanyRepository;
import com.sba302.electroshop.repository.RoleRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.CompanyService;
import com.sba302.electroshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

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

        if (companyRepository.existsByTaxCode(request.getTaxCode())) {
            throw new ResourceConflictException("Company with tax code " + request.getTaxCode() + " already exists");
        }

        Company company = companyMapper.toEntity(request);
        company.setStatus(CompanyStatus.PENDING);
        Company savedCompany = companyRepository.save(company);
        log.info("Company created successfully with id: {}", savedCompany.getCompanyId());
        return companyMapper.toResponse(savedCompany);
    }

    @Override
    @Transactional
    public CreateCompanyResponse createWithUser(CreateCompanyRequest request) {
        log.info("Creating new company with user: {}", request.getCompanyName());

        // 1. Kiểm tra trùng mã số thuế
        if (companyRepository.existsByTaxCode(request.getTaxCode())) {
            throw new ResourceConflictException("Company with tax code " + request.getTaxCode() + " already exists");
        }

        // 2. Kiểm tra user tồn tại
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
 
        if (user.getCompany() != null) {
            throw new ResourceConflictException("User is already associated with a company");
        }

        // 3. Tạo Company với trạng thái PENDING (chờ admin duyệt)
        Company company = companyMapper.toEntity(request);
        company.setStatus(CompanyStatus.PENDING);
        Company savedCompany = companyRepository.save(company);
        log.info("Company created with id: {} and status PENDING", savedCompany.getCompanyId());

        // 4. Cập nhật tài khoản User với role COMPANY và liên kết vào company vừa tạo
        Role companyRole = roleRepository.findByRoleName("COMPANY")
                .orElseThrow(() -> new ResourceNotFoundException("Role COMPANY not found"));
 
        user.setCompany(savedCompany);
        user.setRole(companyRole);
 
        User savedUser = userRepository.save(user);
        log.info("User (COMPANY) with email: {} linked to company id: {} and role updated",
                savedUser.getEmail(), savedCompany.getCompanyId());
 
        // 5. Gửi email xác nhận đăng ký
        emailService.sendCompanyStatusEmail(savedCompany, CompanyStatus.PENDING, null);

        // 6. Trả về response đầy đủ
        return companyMapper.toCreateCompanyResponse(savedCompany, savedUser);
    }

    @Override
    @Transactional
    public CompanyResponse update(Integer id, CompanyRequest request) {
        log.info("Updating company with id: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

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
    public CompanyResponse updateStatus(Integer id, CompanyStatus status) {
        log.info("Updating status for company id: {} to {}", id, status);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        company.setStatus(status);
        if (status == CompanyStatus.APPROVED) {
            company.setApprovedAt(LocalDateTime.now());
        }

        Company updatedCompany = companyRepository.save(company);
        
        if (status != CompanyStatus.PENDING) {
            emailService.sendCompanyStatusEmail(updatedCompany, status, null);
        }

        log.info("Company status updated to {} for id: {}", status, id);
        return companyMapper.toResponse(updatedCompany);
    }

    @Override
    public CompanyResponse getByUserId(Integer userId) {
        log.info("Fetching company registration for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getCompany() == null) {
            throw new ResourceNotFoundException("No company registration found for this user");
        }

        return companyMapper.toResponse(user.getCompany());
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
