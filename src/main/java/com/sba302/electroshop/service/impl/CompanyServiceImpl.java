package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.repository.CompanyRepository;
import com.sba302.electroshop.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    public Optional<Company> findById(Integer id) {
        return companyRepository.findById(id);
    }

    @Override
    @Transactional
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        companyRepository.deleteById(id);
    }
}
