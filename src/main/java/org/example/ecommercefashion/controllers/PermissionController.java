package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.AssignPermissionRequest;
import org.example.ecommercefashion.dtos.request.PermissionRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.PermissionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Permission;
import org.example.ecommercefashion.services.PermissionService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permission")
@RequiredArgsConstructor
@Api(tags = "Permission" , value = "Endpoints for permission management")
public class PermissionController {

  private final PermissionService permissionService;

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public PermissionResponse createPermission(
      @Valid @RequestBody PermissionRequest permissionRequest) {
    return permissionService.createPermission(permissionRequest);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public PermissionResponse getPermissionById(@PathVariable Long id) {
    return permissionService.getPermissionById(id);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void deletePermission(@PathVariable Long id) {
    permissionService.deletePermission(id);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public PermissionResponse updatePermission(
      @PathVariable Long id, @Valid @RequestBody PermissionRequest permissionRequest) {
    return permissionService.updatePermission(id, permissionRequest);
  }

  @PatchMapping("/assign-permission-to-role")
  public MessageResponse assignPermissionToRole(
      @Valid @RequestBody AssignPermissionRequest assignPermissionRequest) {
    return permissionService.assignPermissionToRole(assignPermissionRequest);
  }

  @GetMapping
  public ResponsePage<Permission, PermissionResponse> getAllPermissions(Pageable pageable) {
    return permissionService.getAllPermissions(pageable);
  }
}
