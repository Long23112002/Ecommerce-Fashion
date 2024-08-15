package org.example.ecommercefashion.dtos.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.annotations.ValidPassword;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

  @Email(message = "Email should be valid")
  @NotBlank(message = "Email is required")
  private String email;

  @NotBlank(message = "You must enter your current password!")
  private String currentPassword;

  @ValidPassword private String newPassword;

  @ValidPassword private String confirmNewPassword;

  @AssertTrue(message = "New password and confirm new password do not match!")
  public boolean isPasswordMatches() {
    return newPassword.equals(confirmNewPassword);
  }

  @AssertTrue(message = "New password must be different from the current password!")
  public boolean isNewPasswordNotEqualOldPassword() {
    return !newPassword.equals(currentPassword);
  }
}
