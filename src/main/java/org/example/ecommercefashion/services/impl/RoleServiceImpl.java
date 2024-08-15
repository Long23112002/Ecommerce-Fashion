package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import jakarta.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.request.RoleRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.PermissionResponse;
import org.example.ecommercefashion.dtos.response.RoleResponse;
import org.example.ecommercefashion.entities.Permission;
import org.example.ecommercefashion.entities.Role;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
  private final EntityManager entityManager;

  @Override
  @Transactional
  public RoleResponse createRole(RoleRequest roleRequest) {
    Role role = new Role();
    FnCommon.coppyNonNullProperties(role, roleRequest);

    Set<Permission> permissionSet = new HashSet<>();
    for (Long permissionId : roleRequest.getPermissionIds()) {
      Permission permissionEntity =
          Optional.ofNullable(entityManager.find(Permission.class, permissionId))
              .orElseThrow(
                  () ->
                      new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PERMISSION_NOT_FOUND));
      permissionSet.add(permissionEntity);
    }

    role.setPermissions(permissionSet);
    entityManager.persist(role);
    return mapRoleToRoleResponse(role);
  }

  @Override
  @Transactional
  public RoleResponse updateRole(Long id, RoleRequest roleRequest) {
    Optional<Role> role =
        Optional.ofNullable(
            Optional.of(entityManager.find(Role.class, id))
                .orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ROLE_NOT_FOUND)));

    FnCommon.coppyNonNullProperties(role, roleRequest);
    entityManager.merge(role);
    return mapRoleToRoleResponse(role.get());
  }

  @Override
  public RoleResponse getRoleById(Long id) {
    log.info("Role: {}", id);
    Role role = entityManager.find(Role.class, id);

    if (role == null) {
      throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ROLE_NOT_FOUND);
    }
    return mapRoleToRoleResponse(role);
  }

  @Override
  public MessageResponse deleteRole(Long id) {
    Role role = entityManager.find(Role.class, id);
    if (role == null) {
      throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ROLE_NOT_FOUND);
    }
    entityManager.remove(role);
    return MessageResponse.builder().message("Role delete successfully").build();
  }

  private RoleResponse mapRoleToRoleResponse(Role role) {
    RoleResponse roleResponse = new RoleResponse();
    FnCommon.coppyNonNullProperties(roleResponse, role);
    roleResponse.setPermissions(mapPermissionToPermissionResponse(role.getPermissions()));
    return roleResponse;
  }

  private Set<PermissionResponse> mapPermissionToPermissionResponse(Set<Permission> permissions) {
    Set<PermissionResponse> permissionResponses = new HashSet<>();
    for (Permission permission : permissions) {
      PermissionResponse permissionResponse = new PermissionResponse();
      FnCommon.coppyNonNullProperties(permissionResponse, permission);
      permissionResponses.add(permissionResponse);
    }
    return permissionResponses;
  }
}
