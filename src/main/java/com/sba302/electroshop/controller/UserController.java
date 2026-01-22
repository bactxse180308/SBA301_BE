package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.User;
import com.sba302.electroshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<User>> getAll() {
        return ApiResponse.success(userService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getById(@PathVariable Integer id) {
        return userService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "User not found"));
    }

    @PostMapping
    public ApiResponse<User> create(@RequestBody User user) {
        return ApiResponse.success(userService.save(user));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        userService.deleteById(id);
        return ApiResponse.success(null);
    }
}
