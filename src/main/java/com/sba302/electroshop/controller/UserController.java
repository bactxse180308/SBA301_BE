package com.sba302.electroshop.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sba302.electroshop.dto.request.CreateUserRequest;
import com.sba302.electroshop.dto.request.UpdateUserRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.UserResponse;
import com.sba302.electroshop.enums.UserStatus;
import com.sba302.electroshop.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user management (Admin only)")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user by ID", description = "Retrieve user details by user ID (Admin only)")
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(userService.getById(id));
    }

    @Operation(summary = "Get user by email", description = "Retrieve user details by email address (Admin only)")
    @GetMapping("/email/{email}")
    public ApiResponse<UserResponse> getByEmail(@PathVariable String email) {
        return ApiResponse.success(userService.getByEmail(email));
    }

    @Operation(summary = "Search users", description = "Search users with optional filters: email, phoneNumber and status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<Page<UserResponse>> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) UserStatus status,
            @Parameter(description = "Pagination and sorting parameters. Default sort: fullName,asc", 
                       schema = @Schema(example = "{\"page\": 0, \"size\": 20, \"sort\": [\"fullName,asc\"]}"))
            @PageableDefault(size = 20, sort = "fullName", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.success(userService.search(email, phoneNumber, status, pageable));
    }

    @Operation(summary = "Create new user", description = "Create a new user with role assignment (Admin only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    @Operation(summary = "Update user", description = "Update user information by ID (Admin only)")
    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.success(userService.update(id, request));
    }

    @Operation(summary = "Update user status", description = "Change user status (ACTIVE/INACTIVE) (Admin only)")
    @PatchMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @PathVariable Integer id,
            @RequestParam UserStatus status) {
        userService.updateStatus(id, status);
        return ApiResponse.success(null);
    }

    @Operation(summary = "Add reward points", description = "Add reward points to user account (Admin only)")
    @PostMapping("/{id}/reward-points")
    public ApiResponse<Void> addRewardPoints(
            @PathVariable Integer id,
            @RequestParam Integer points) {
        userService.addRewardPoints(id, points);
        return ApiResponse.success(null);
    }

    @Operation(summary = "Delete user", description = "Delete user by ID (Admin only)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return ApiResponse.success(null);
    }
}
