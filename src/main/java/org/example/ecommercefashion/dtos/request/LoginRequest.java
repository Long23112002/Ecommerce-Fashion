package org.example.ecommercefashion.dtos.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email is invalid")
  private String email;

  @NotBlank(message = "Password is required")
  private String password;
}
