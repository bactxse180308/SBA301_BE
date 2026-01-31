package com.sba302.electroshop.mapper;

import com.sba302.electroshop.dto.request.CreateUserRequest;
import com.sba302.electroshop.dto.request.UpdateUserRequest;
import com.sba302.electroshop.dto.response.UserResponse;
import com.sba302.electroshop.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Map CreateUserRequest to User Entity
     * Password and role must be set manually in service layer
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "rewardPoint", constant = "0")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    User toEntity(CreateUserRequest request);

    /**
     * Map User Entity to UserResponse
     */
    @Mapping(target = "role", source = "role.roleName")
    @Mapping(target = "status", expression = "java(user.getStatus() != null ? user.getStatus().name() : null)")
    @Mapping(target = "isActive", expression = "java(user.getStatus() == com.sba302.electroshop.enums.UserStatus.ACTIVE)")
    UserResponse toResponse(User user);

    /**
     * Update existing User entity from UpdateUserRequest
     * Ignores userId, email, password, role, registrationDate, rewardPoint, status
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "rewardPoint", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget User entity, UpdateUserRequest request);
}
