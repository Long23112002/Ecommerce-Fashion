
package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.ChangePasswordRequest;
import org.example.ecommercefashion.dtos.request.UserRequest;
import org.example.ecommercefashion.dtos.request.UserRoleAssignRequest;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.services.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Api(tags = "User", value = "Endpoints for user management")
public class UserController {

  private final UserService userService;

  @PostMapping
  public UserResponse createUser(@Valid @RequestBody UserRequest userRequest) {
    return userService.createUser(userRequest);
  }

  @PutMapping("/{id}")
  public UserResponse updateUser(
      @PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
    return userService.updateUser(id, userRequest);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }

  @GetMapping("/{id}")
  public UserResponse getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @PatchMapping("/assign-role-admin")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public void assignRoleAdmin(@Valid @RequestBody String email) {
    userService.assignRoleAdmin(email);
  }

  @PatchMapping("/change-password")
  public void changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    userService.changePassword(changePasswordRequest);
  }

  @GetMapping
  public ResponsePage<User, UserResponse> getAllUsers(UserParam param, Pageable pageable) {
    return userService.getAllUsers(param, pageable);
  }

  @PatchMapping("/assign-user-role")
  public void assignUserRole(@Valid @RequestBody UserRoleAssignRequest userRoleAssignRequest) {
    userService.assignUserRole(userRoleAssignRequest);
  }

  public static void main(String[] args) {
    String password = "12345678";
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(password);
    System.out.printf(encodedPassword);
  }
}

