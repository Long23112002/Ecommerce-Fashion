package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Permission;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {

  private Long id;

  private String name;

  private Set<PermissionResponse> permissions;
}
