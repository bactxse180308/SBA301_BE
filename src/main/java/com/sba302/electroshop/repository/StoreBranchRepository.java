package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.StoreBranch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreBranchRepository extends JpaRepository<StoreBranch, Integer> {
    Page<StoreBranch> findByBranchNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String keyword, String keyword1, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT s FROM StoreBranch s WHERE LOWER(s.branchName) LIKE :keyword OR LOWER(s.address) LIKE :keyword")
    java.util.List<StoreBranch> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    boolean existsByBranchNameIgnoreCase(String branchName);
}
