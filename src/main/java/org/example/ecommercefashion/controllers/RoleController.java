package org.example.ecommercefashion.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.RoleRequest;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.RoleResponse;
import org.example.ecommercefashion.entities.Role;
import org.example.ecommercefashion.services.RoleService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Endpoints for role management")
public class RoleController {

  private final RoleService roleService;

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public RoleResponse createRole(@Valid @RequestBody RoleRequest roleRequest) {
    return roleService.createRole(roleRequest);
  }

  @GetMapping("/{id}")
  public RoleResponse getRoleById(@PathVariable Long id) {
    return roleService.getRoleById(id);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public RoleResponse updateRole(
      @PathVariable Long id, @Valid @RequestBody RoleRequest roleRequest) {
    return roleService.updateRole(id, roleRequest);
  }

  @GetMapping
  public ResponsePage<Role, RoleResponse> filterRoles(String keyword, Pageable pageable) {
    return roleService.filterRoles(keyword, pageable);
  }
}
