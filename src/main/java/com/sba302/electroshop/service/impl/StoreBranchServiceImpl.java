package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.StoreBranch;
import com.sba302.electroshop.repository.StoreBranchRepository;
import com.sba302.electroshop.service.StoreBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreBranchServiceImpl implements StoreBranchService {

    private final StoreBranchRepository storeBranchRepository;

    @Override
    public List<StoreBranch> findAll() {
        return storeBranchRepository.findAll();
    }

    @Override
    public Optional<StoreBranch> findById(Integer id) {
        return storeBranchRepository.findById(id);
    }

    @Override
    @Transactional
    public StoreBranch save(StoreBranch storeBranch) {
        return storeBranchRepository.save(storeBranch);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        storeBranchRepository.deleteById(id);
    }
}
