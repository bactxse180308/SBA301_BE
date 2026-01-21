package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Customer;
import com.sba302.electroshop.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ApiResponse<List<Customer>> getAll() {
        return ApiResponse.success(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Customer> getById(@PathVariable Integer id) {
        return customerService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Customer not found"));
    }

    @PostMapping
    public ApiResponse<Customer> create(@RequestBody Customer customer) {
        return ApiResponse.success(customerService.save(customer));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        customerService.deleteById(id);
        return ApiResponse.success(null);
    }
}
