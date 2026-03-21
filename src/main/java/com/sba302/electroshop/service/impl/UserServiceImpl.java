package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateUserRequest;
import com.sba302.electroshop.dto.request.UpdateUserRequest;
import com.sba302.electroshop.dto.response.UserResponse;
import com.sba302.electroshop.entity.Role;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.exception.ResourceConflictException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.UserMapper;
import com.sba302.electroshop.repository.RoleRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.service.UserService;
import com.sba302.electroshop.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.sba302.electroshop.repository.OrderRepository orderRepository;
    private final com.sba302.electroshop.repository.ReviewRepository reviewRepository;
    private final com.sba302.electroshop.repository.WishlistRepository wishlistRepository;
    private final com.sba302.electroshop.repository.WishlistItemRepository wishlistItemRepository;

    @Override
    public UserResponse getById(Integer id) {
        log.info("Getting user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getByEmail(String email) {
        log.info("Getting user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    public Page<UserResponse> search(String email, String phoneNumber, UserStatus status, String roleName, Pageable pageable) {
        log.info("Searching users with filters - email: {}, phoneNumber: {}, status: {}, role: {}", email, phoneNumber, status, roleName);
        
        try {
            // Build specification with filters
            Specification<User> spec = UserSpecification.filterUsers(email, phoneNumber, status, roleName);
            
            // Execute query with specification
            Page<User> users = userRepository.findAll(spec, pageable);
            
            log.info("Found {} users matching search criteria", users.getTotalElements());
            
            // Map to response
            return users.map(userMapper::toResponse);
            
        } catch (Exception e) {
            log.error("Error occurred while searching users with filters - email: {}, phoneNumber: {}, status: {}, role: {}", 
                    email, phoneNumber, status, roleName, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Email already exists: " + request.getEmail());
        }

        // Find role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + request.getRoleId()));

        // Map to entity using mapper
        User user = userMapper.toEntity(request);

        // Set password (encoded), role, and status manually
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        // Save
        user = userRepository.save(user);
        log.info("User created successfully with id: {}", user.getUserId());

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse update(Integer id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        // Use mapper to update entity
        userMapper.updateEntity(user, request);

        // Save and return
        user = userRepository.save(user);
        log.info("User updated successfully with id: {}", id);

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void updateStatus(Integer id, UserStatus status) {
        log.info("Updating status for user id: {} to {}", id, status);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void addRewardPoints(Integer userId, Integer points) {
        log.info("Adding {} points to user id: {}", points, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        int currentPoints = user.getRewardPoint() != null ? user.getRewardPoint() : 0;
        user.setRewardPoint(currentPoints + points);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public com.sba302.electroshop.dto.response.UserDashboardResponse getDashboard(Integer id) {
        log.info("Getting dashboard data for user id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        UserResponse userInfo = userMapper.toResponse(user);

        Long totalOrders = orderRepository.countOrdersByUserId(id);
        java.math.BigDecimal totalSpent = orderRepository.sumSpentByUserId(id);
        if (totalSpent == null) totalSpent = java.math.BigDecimal.ZERO;

        org.springframework.data.domain.Pageable recentItemsPage = org.springframework.data.domain.PageRequest.of(0, 3);
        
        java.util.List<com.sba302.electroshop.dto.response.RecentOrderResponse> recentOrders = orderRepository.findRecentOrdersByUserId(id, recentItemsPage);
        
        java.util.List<com.sba302.electroshop.entity.Review> recentReviewEntities = reviewRepository.findRecentReviewsByUserId(id, recentItemsPage);
        java.util.List<com.sba302.electroshop.dto.response.ReviewResponse> recentReviews = recentReviewEntities.stream()
            .map(r -> com.sba302.electroshop.dto.response.ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .userId(user.getUserId())
                .userFullName(user.getFullName())
                .productId(r.getProduct().getProductId())
                .productName(r.getProduct().getProductName())
                .rating(r.getRating())
                .comment(r.getComment())
                .reviewDate(r.getReviewDate())
                .replyComment(r.getReplyComment())
                .replyDate(r.getReplyDate())
                .repliedByUserId(r.getRepliedBy() != null ? r.getRepliedBy().getUserId() : null)
                .repliedByFullName(r.getRepliedBy() != null ? r.getRepliedBy().getFullName() : null)
                .build())
            .toList();

        java.util.List<com.sba302.electroshop.dto.response.WishlistResponse.WishlistItemResponse> wishlistItems = new java.util.ArrayList<>();
        wishlistRepository.findFirstByUser_UserId(id).ifPresent(w -> {
            java.util.List<com.sba302.electroshop.entity.WishlistItem> items = wishlistItemRepository.findRecentItemsByWishlistId(w.getWishlistId(), recentItemsPage);
            items.forEach(item -> wishlistItems.add(
                com.sba302.electroshop.dto.response.WishlistResponse.WishlistItemResponse.builder()
                    .productId(item.getProduct().getProductId())
                    .productName(item.getProduct().getProductName())
                    .productImageUrl(item.getProduct().getMainImage())
                    .createdDate(item.getCreatedDate())
                    .build()
            ));
        });

        return com.sba302.electroshop.dto.response.UserDashboardResponse.builder()
                .userInfo(userInfo)
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .recentOrders(recentOrders)
                .recentReviews(recentReviews)
                .wishlist(wishlistItems)
                .build();
    }
}
