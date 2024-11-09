package org.example.ecommercefashion.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.PermissionGroupResponse;
import org.example.ecommercefashion.services.PermissionGroupService;
import org.example.ecommercefashion.utils.ResponsePageV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permission_group")
@RequiredArgsConstructor
public class PermissionGroupController {
  @Autowired private PermissionGroupService permissionGroupService;

  @GetMapping
  public ResponsePageV2<PermissionGroupResponse> filter(Pageable pageable) {
    return new ResponsePageV2<>(permissionGroupService.filter(pageable));
  }
}
