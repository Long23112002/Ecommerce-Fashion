package org.example.ecommercefashion.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.RoleRequest;
import org.example.ecommercefashion.dtos.response.RoleResponse;
import org.example.ecommercefashion.services.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Endpoints for role management")
public class RoleController {

  private final RoleService roleService;

  @PostMapping
  @PreAuthorize("hasRole('STAFF') AND hasAuthority('CREATE_PRODUCT')")
  public RoleResponse createRole(@Valid @RequestBody RoleRequest roleRequest) {
    return roleService.createRole(roleRequest);
  }

  @GetMapping("/{id}")
  public RoleResponse getRoleById(@PathVariable Long id) {
    return roleService.getRoleById(id);
  }

  @DeleteMapping("/{id}")
  public void deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
  }

  @PutMapping("/{id}")
  public RoleResponse updateRole(
      @PathVariable Long id, @Valid @RequestBody RoleRequest roleRequest) {
    return roleService.updateRole(id, roleRequest);
  }
}
