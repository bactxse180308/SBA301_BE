package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Company;
import java.util.List;
import java.util.Optional;

public interface CompanyService {
    List<Company> findAll();

    Optional<Company> findById(Integer id);

    Company save(Company company);

    void deleteById(Integer id);
}
