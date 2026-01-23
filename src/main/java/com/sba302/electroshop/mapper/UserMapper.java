package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateUserRequest;
import com.sba302.electroshop.dto.response.UserResponse;
import com.sba302.electroshop.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "rewardPoint", constant = "0")
    @Mapping(target = "role", ignore = true) // Handle role explicitly or ignore for now
    @Mapping(target = "password", ignore = true) // Handle password separately
    User toEntity(CreateUserRequest request);

    @Mapping(target = "role", source = "role.roleName")
    UserResponse toResponse(User user);
}
