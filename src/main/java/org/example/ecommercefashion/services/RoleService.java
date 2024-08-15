package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.RoleRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.RoleResponse;

public interface RoleService {

  RoleResponse createRole(RoleRequest roleRequest);

  RoleResponse updateRole(Long id, RoleRequest roleRequest);

  RoleResponse getRoleById(Long id);

  MessageResponse deleteRole(Long id);
}
