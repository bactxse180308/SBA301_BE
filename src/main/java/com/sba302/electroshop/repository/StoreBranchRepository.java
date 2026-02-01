package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.StoreBranch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreBranchRepository extends JpaRepository<StoreBranch, Integer> {
    Page<StoreBranch> findByBranchNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String keyword, String keyword1, Pageable pageable);

    boolean existsByBranchNameIgnoreCase(@NotBlank(message = "Branch name is required") @Size(min = 2, max = 255, message = "Branch name must be between 2 and 255 characters") String branchName);
}
