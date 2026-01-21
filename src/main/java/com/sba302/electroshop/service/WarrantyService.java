package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.Warranty;
import java.util.List;
import java.util.Optional;

public interface WarrantyService {
    List<Warranty> findAll();

    Optional<Warranty> findById(Integer id);

    Warranty save(Warranty warranty);

    void deleteById(Integer id);
}
