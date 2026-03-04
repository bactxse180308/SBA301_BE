package com.sba302.electroshop.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sba302.electroshop.dto.response.WishlistResponse;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.entity.Wishlist;
import com.sba302.electroshop.entity.WishlistItem;
import com.sba302.electroshop.exception.ResourceConflictException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.repository.MediaRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.UserRepository;
import com.sba302.electroshop.repository.WishlistItemRepository;
import com.sba302.electroshop.repository.WishlistRepository;
import com.sba302.electroshop.service.WishlistService;
import com.sba302.electroshop.specification.WishlistItemSpecification;
import com.sba302.electroshop.specification.WishlistSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MediaRepository mediaRepository;

    @Override
    public WishlistResponse getByUser(Integer userId) {
        log.info("Getting wishlist for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Wishlist wishlist = wishlistRepository.findOne(WishlistSpecification.hasUser(userId))
                .orElseGet(() -> createWishlistForUser(user));
        
        List<WishlistItem> items = wishlistItemRepository.findAll(
                WishlistItemSpecification.hasWishlist(wishlist.getWishlistId()));
        
        return mapToResponse(wishlist, items);
    }

    @Override
    @Transactional
    public WishlistResponse addItem(Integer userId, Integer productId) {
        log.info("Adding product {} to wishlist for user: {}", productId, userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        Wishlist wishlist = wishlistRepository.findOne(WishlistSpecification.hasUser(userId))
                .orElseGet(() -> createWishlistForUser(user));
        
        // Check if product already in wishlist
        boolean exists = wishlistItemRepository.exists(
                WishlistItemSpecification.hasWishlistAndProduct(wishlist.getWishlistId(), productId));
        
        if (exists) {
            throw new ResourceConflictException("Product already in wishlist");
        }
        
        WishlistItem item = WishlistItem.builder()
                .wishlist(wishlist)
                .product(product)
                .createdDate(LocalDateTime.now())
                .build();
        
        wishlistItemRepository.save(item);
        
        List<WishlistItem> items = wishlistItemRepository.findAll(
                WishlistItemSpecification.hasWishlist(wishlist.getWishlistId()));
        
        return mapToResponse(wishlist, items);
    }

    @Override
    @Transactional
    public void removeItem(Integer userId, Integer productId) {
        log.info("Removing product {} from wishlist for user: {}", productId, userId);
        
        Wishlist wishlist = wishlistRepository.findOne(WishlistSpecification.hasUser(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user: " + userId));
        
        WishlistItem item = wishlistItemRepository.findOne(
                WishlistItemSpecification.hasWishlistAndProduct(wishlist.getWishlistId(), productId))
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in wishlist"));
        
        wishlistItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearWishlist(Integer userId) {
        log.info("Clearing wishlist for user: {}", userId);
        
        Wishlist wishlist = wishlistRepository.findOne(WishlistSpecification.hasUser(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found for user: " + userId));
        
        wishlistItemRepository.deleteByWishlistId(wishlist.getWishlistId());
    }

    @Override
    public boolean isProductInWishlist(Integer userId, Integer productId) {
        log.info("Checking if product {} is in wishlist for user: {}", productId, userId);
        
        Wishlist wishlist = wishlistRepository.findOne(WishlistSpecification.hasUser(userId))
                .orElse(null);
        
        if (wishlist == null) {
            return false;
        }
        
        return wishlistItemRepository.exists(
                WishlistItemSpecification.hasWishlistAndProduct(wishlist.getWishlistId(), productId));
    }

    private Wishlist createWishlistForUser(User user) {
        log.info("Creating new wishlist for user: {}", user.getUserId());
        
        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .createdDate(LocalDateTime.now())
                .build();
        
        return wishlistRepository.save(wishlist);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist, List<WishlistItem> items) {
        List<WishlistResponse.WishlistItemResponse> itemResponses = items.stream()
                .map(item -> {
                    String imageUrl = mediaRepository.findFirstByProductId(item.getProduct().getProductId())
                            .map(media -> media.getUrl())
                            .orElse(null);
                    
                    return WishlistResponse.WishlistItemResponse.builder()
                            .productId(item.getProduct().getProductId())
                            .productName(item.getProduct().getProductName())
                            .productImageUrl(imageUrl)
                            .createdDate(item.getCreatedDate())
                            .build();
                })
                .collect(Collectors.toList());
        
        return WishlistResponse.builder()
                .wishlistId(wishlist.getWishlistId())
                .userId(wishlist.getUser().getUserId())
                .createdDate(wishlist.getCreatedDate())
                .items(itemResponses)
                .build();
    }
}
