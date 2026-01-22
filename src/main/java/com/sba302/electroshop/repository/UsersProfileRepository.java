package com.sba302.electroshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sba302.electroshop.entity.UsersProfile;

@Repository
public interface UsersProfileRepository extends JpaRepository<UsersProfile, Integer> {
}
