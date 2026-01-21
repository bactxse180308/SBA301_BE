package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> findAll();

    Optional<Category> findById(Integer id);

    Category save(Category category);

    void deleteById(Integer id);
}
