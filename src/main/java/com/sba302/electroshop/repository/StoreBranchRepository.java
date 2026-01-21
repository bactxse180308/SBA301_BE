package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.StoreBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreBranchRepository extends JpaRepository<StoreBranch, Integer> {
}
