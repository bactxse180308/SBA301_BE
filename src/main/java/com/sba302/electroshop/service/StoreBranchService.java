package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.StoreBranch;
import java.util.List;
import java.util.Optional;

public interface StoreBranchService {
    List<StoreBranch> findAll();

    Optional<StoreBranch> findById(Integer id);

    StoreBranch save(StoreBranch storeBranch);

    void deleteById(Integer id);
}
