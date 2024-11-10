package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.response.PermissionGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PermissionGroupService {

  Page<PermissionGroupResponse> filter(Pageable pageable);
}
