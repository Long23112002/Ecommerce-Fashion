package org.example.ecommercefashion.dtos.request;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {

  @NotBlank(message = "Role name is required")
  @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
  private String name;

  private String description;

  @NotEmpty(message = "Permission ids is required")
  private List<Long> permissionIds;
}
