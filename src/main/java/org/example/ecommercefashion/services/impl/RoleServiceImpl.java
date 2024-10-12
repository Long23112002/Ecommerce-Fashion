package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.request.RoleRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.PermissionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.RoleResponse;
import org.example.ecommercefashion.entities.Permission;
import org.example.ecommercefashion.entities.Role;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.RefreshTokenRepository;
import org.example.ecommercefashion.repositories.RoleRepository;
import org.example.ecommercefashion.services.NotificationService;
import org.example.ecommercefashion.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

  private final EntityManager entityManager;
  private final RoleRepository roleRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final NotificationService notificationService;

  @Override
  public ResponsePage<Role, RoleResponse> filterRoles(String keyword, Pageable pageable) {
    Pageable sortedPageable =
        PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
    Page<Role> pageRole = roleRepository.filterRoles(keyword, sortedPageable);
    return new ResponsePage<>(pageRole, RoleResponse.class);
  }

  @Override
  @Transactional
  public RoleResponse createRole(RoleRequest roleRequest) {
    String roleName = roleRequest.getName();
    Role existingRole =
        entityManager
            .createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
            .setParameter("name", roleName)
            .getResultStream()
            .findFirst()
            .orElse(null);

    if (existingRole != null) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ROLE_EXISTED);
    }

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
    if (roleRepository.existsByName(roleRequest.getName())) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ROLE_EXISTED);
    }
    Role role =
        roleRepository
            .findById(id)
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ROLE_NOT_FOUND));

    FnCommon.coppyNonNullProperties(role, roleRequest);
    role = entityManager.merge(role);
    return mapRoleToRoleResponse(role);
  }

  @Override
  public RoleResponse getRoleById(Long id) {
    Role role = entityManager.find(Role.class, id);

    if (role == null) {
      throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ROLE_NOT_FOUND);
    }
    return mapRoleToRoleResponse(role);
  }

  @Override
  @Transactional
  public MessageResponse deleteRole(Long id) {
    Role role = entityManager.find(Role.class, id);
    if (role == null) {
      throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ROLE_NOT_FOUND);
    }


    for (User user : role.getUsers()) {
      user.getRoles().remove(role);
      refreshTokenRepository.deleteAllByUserId(user.getId());
    }
    entityManager.remove(role);
    return MessageResponse.builder().message("Role deleted successfully").build();
  }


  private RoleResponse mapRoleToRoleResponse(Role role) {
    RoleResponse roleResponse = new RoleResponse();
    FnCommon.coppyNonNullProperties(roleResponse, role);
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
