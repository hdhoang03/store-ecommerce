package com.example.store.mapper;

import com.example.store.dto.request.UserCreationRequest;
import com.example.store.dto.request.UserUpdateRequest;
import com.example.store.dto.response.UserResponse;
import com.example.store.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "email", source = "email")
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
