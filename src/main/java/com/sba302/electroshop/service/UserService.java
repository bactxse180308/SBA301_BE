package com.sba302.electroshop.service;

import com.sba302.electroshop.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();

    Optional<User> findById(Integer id);

    User save(User user);

    void deleteById(Integer id);
}
