package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Attribute;
import java.util.List;
import java.util.Optional;

public interface AttributeService {
    List<Attribute> findAll();

    Optional<Attribute> findById(Integer id);

    Attribute save(Attribute attribute);

    void deleteById(Integer id);
}
