package org.example.ecommercefashion.dtos.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Permission;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionGroupResponse {

  private Long id;

  private String name;

  private List<Permission> permissions;
}
