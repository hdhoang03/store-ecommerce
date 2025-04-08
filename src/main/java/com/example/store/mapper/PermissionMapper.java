package com.example.store.mapper;

import com.example.store.dto.request.PermissionRequest;
import com.example.store.dto.response.PermissionResponse;
import com.example.store.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
