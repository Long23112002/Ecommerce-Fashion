package org.example.ecommercefashion.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleAssignRequest {

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email is required")
  private String email;

  @NotEmpty(message = "Role ids are required")
  private List<Long> roleIds;
}
