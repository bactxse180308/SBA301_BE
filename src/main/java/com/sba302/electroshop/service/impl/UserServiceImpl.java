package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateUserRequest;
import com.sba302.electroshop.dto.request.UpdateUserRequest;
import com.sba302.electroshop.dto.response.UserResponse;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.mapper.UserMapper;
import com.sba302.electroshop.repository.RoleRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public UserResponse getByEmail(String email) {
        // TODO: Implement - find by email, map to response
        return null;
    }

    @Override
    public Page<UserResponse> search(String keyword, UserStatus status, Pageable pageable) {
        // TODO: Implement - search with optional filters (keyword, status)
        return null;
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        // TODO: Implement - map to entity, encode password, save
        return null;
    }

    @Override
    @Transactional
    public UserResponse update(Integer id, UpdateUserRequest request) {
        // TODO: Implement - find, update entity, save
        return null;
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, UserStatus status) {
        // TODO: Implement - update user status
    }

    @Override
    @Transactional
    public void addRewardPoints(Integer userId, Integer points) {
        // TODO: Implement - add reward points to user
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete user by id
    }
}
