package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.RoleRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.RoleResponse;
import org.example.ecommercefashion.entities.Role;
import org.springframework.data.domain.Pageable;

public interface RoleService {

  ResponsePage<Role , RoleResponse> filterRoles(String keyword,Pageable pageable);

  RoleResponse createRole(RoleRequest roleRequest);

  RoleResponse updateRole(Long id, RoleRequest roleRequest);

  RoleResponse getRoleById(Long id);

  MessageResponse deleteRole(Long id);
}
