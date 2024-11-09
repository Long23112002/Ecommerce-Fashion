package org.example.ecommercefashion.services.impl;

import java.util.List;
import org.example.ecommercefashion.dtos.response.PermissionGroupResponse;
import org.example.ecommercefashion.entities.Permission;
import org.example.ecommercefashion.entities.PermissionGroup;
import org.example.ecommercefashion.repositories.PermissionGroupRepository;
import org.example.ecommercefashion.repositories.PermissionRepository;
import org.example.ecommercefashion.services.PermissionGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PermissionGroupServiceImpl implements PermissionGroupService {

  @Autowired private PermissionGroupRepository permissionGroupRepository;

  @Autowired private PermissionRepository permissionRepository;

  @Override
  public Page<PermissionGroupResponse> filter(Pageable pageable) {
    Page<PermissionGroup> permissionGroupPage = permissionGroupRepository.findAll(pageable);
    return mapToPermissionGroupResponse(permissionGroupPage, pageable);
  }

  private Page<PermissionGroupResponse> mapToPermissionGroupResponse(
      Page<PermissionGroup> permissionGroupPage, Pageable pageable) {
    permissionGroupPage = permissionGroupRepository.findAll(pageable);
    return permissionGroupPage.map(
        permissionGroup -> {
          PermissionGroupResponse permissionGroupResponse = new PermissionGroupResponse();
          permissionGroupResponse.setId(permissionGroup.getId());
          permissionGroupResponse.setName(permissionGroup.getName());
          permissionGroupResponse.setPermissions(getPermission(permissionGroup.getGroupIds()));
          return permissionGroupResponse;
        });
  }

  private List<Permission> getPermission(List<Long> ids) {
    return permissionRepository.findAllById(ids);
  }
}
