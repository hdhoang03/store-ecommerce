package com.example.store.mapper;

import com.example.store.dto.request.RoleRequest;
import com.example.store.dto.response.RoleResponse;
import com.example.store.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);//dto -> entity
    RoleResponse toRoleResponse(Role role);//entity -> dto
}
