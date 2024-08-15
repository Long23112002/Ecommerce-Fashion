package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.PermissionRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.PermissionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Permission;
import org.springframework.data.domain.Pageable;

public interface PermissionService {

  PermissionResponse createPermission(PermissionRequest permissionRequest);

  PermissionResponse getPermissionById(Long id);

  MessageResponse deletePermission(Long id);

  PermissionResponse updatePermission(Long id, PermissionRequest permissionRequest);

  ResponsePage<Permission, PermissionResponse> getAllPermissions(Pageable pageable);
}
