package com.sba302.electroshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sba302.electroshop.dto.request.CreateUserRequest;
import com.sba302.electroshop.dto.request.UpdateUserRequest;
import com.sba302.electroshop.dto.response.UserResponse;
import com.sba302.electroshop.enums.UserStatus;

public interface UserService {

    UserResponse getById(Integer id);

    UserResponse getByEmail(String email);

    Page<UserResponse> search(String email, String phoneNumber, UserStatus status, Pageable pageable);

    UserResponse create(CreateUserRequest request);

    UserResponse update(Integer id, UpdateUserRequest request);

    void updateStatus(Integer id, UserStatus status);

    void addRewardPoints(Integer userId, Integer points);

    void delete(Integer id);
}
