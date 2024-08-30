package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.AssignPermissionRequest;
import org.example.ecommercefashion.dtos.request.PermissionRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.PermissionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Permission;
import org.example.ecommercefashion.entities.Role;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.PermissionRepository;
import org.example.ecommercefashion.services.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

  private final EntityManager entityManager;
  private final PermissionRepository permissionRepository;

  @Override
  @Transactional
  public PermissionResponse createPermission(PermissionRequest permissionRequest) {
    Permission permission = new Permission();
    FnCommon.coppyNonNullProperties(permission, permissionRequest);
    entityManager.persist(permission);
    return mapPermissionToPermissionResponse(permission);
  }

  @Override
  @Transactional
  public PermissionResponse updatePermission(Long id, PermissionRequest permissionRequest) {
    Permission permission =
        Optional.ofNullable(entityManager.find(Permission.class, id))
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PERMISSION_NOT_FOUND));
    FnCommon.coppyNonNullProperties(permission, permissionRequest);
    entityManager.merge(permission);
    return mapPermissionToPermissionResponse(permission);
  }

  @Override
  public ResponsePage<Permission, PermissionResponse> getAllPermissions(Pageable pageable) {
    Page<Permission> permissionPage = permissionRepository.findAll(pageable);
    return new ResponsePage<>(permissionPage, PermissionResponse.class);
  }

  @Override
  @Transactional
  public MessageResponse assignPermissionToRole(AssignPermissionRequest assignPermissionRequest) {
    Role role =
        Optional.ofNullable(entityManager.find(Role.class, assignPermissionRequest.getRoleId()))
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ROLE_NOT_FOUND));
    Set<Permission> permissions = new HashSet<>();
    for (Long permissionId : assignPermissionRequest.getPermissionIds()) {
      Permission permission =
          Optional.ofNullable(entityManager.find(Permission.class, permissionId))
              .orElseThrow(
                  () ->
                      new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PERMISSION_NOT_FOUND));
      permissions.add(permission);
    }
    role.setPermissions(permissions);
    entityManager.merge(role);
    return MessageResponse.builder().message("Assign permission to role successfully").build();
  }

  @Override
  public PermissionResponse getPermissionById(Long id) {
    Permission permission =
        Optional.ofNullable(entityManager.find(Permission.class, id))
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PERMISSION_NOT_FOUND));
    return mapPermissionToPermissionResponse(permission);
  }

  @Override
  @Transactional
  public MessageResponse deletePermission(Long id) {
    Permission permission =
        Optional.ofNullable(entityManager.find(Permission.class, id))
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PERMISSION_NOT_FOUND));
    entityManager.remove(permission);
    return MessageResponse.builder().message("Permission delete successfully").build();
  }

  private PermissionResponse mapPermissionToPermissionResponse(Permission permission) {
    PermissionResponse permissionResponse = new PermissionResponse();
    permissionResponse.setId(permission.getId());
    permissionResponse.setName(permission.getName());
    return permissionResponse;
  }
}
