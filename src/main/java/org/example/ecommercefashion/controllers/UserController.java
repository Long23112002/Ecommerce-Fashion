package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.annotations.CheckPermission;
import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.ChangePasswordRequest;
import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.dtos.request.UserInfoUpdateRequest;
import org.example.ecommercefashion.dtos.request.UserRequest;
import org.example.ecommercefashion.dtos.request.UserRoleAssignRequest;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.services.UserService;
import org.quartz.JobExecutionException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Api(tags = "User", value = "Endpoints for user management")
public class UserController {

  private final UserService userService;

  public static void main(String[] args) {
    String password = "12345678";
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(password);
    System.out.printf(encodedPassword);
  }

  @PostMapping
  public UserResponse createUser(@Valid @RequestBody UserRequest userRequest)
      throws JobExecutionException {
    return userService.createUser(userRequest);
  }

  @PutMapping("/{id}")
  public UserResponse updateUser(
      @PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
    return userService.updateUser(id, userRequest);
  }

  @PutMapping("/update-info/{id}")
  public UserResponse updateUser(
      @PathVariable Long id,
      @Valid @RequestBody UserInfoUpdateRequest userUpdateRequest,
      @RequestHeader("Authorization") String token) {
    return userService.updateUser(id, userUpdateRequest, token);
  }

  @DeleteMapping("/{id}")
  @CheckPermission({"delete_user"})
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }

  @GetMapping("/{id}")
  public UserResponse getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @PatchMapping("/assign-role-admin")
  @CheckPermission({"assign_role_admin"})
  public void assignRoleAdmin(@Valid @RequestBody String email) {
    userService.assignRoleAdmin(email);
  }

  @PutMapping("/change-password")
  public void changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    userService.changePassword(changePasswordRequest);
  }

  @GetMapping
  public ResponsePage<User, UserResponse> getAllUsers(UserParam param, PageableRequest pageable) {
    return userService.getAllUsers(param, pageable.toPageable());
  }

  @PatchMapping("/assign-user-role")
  public void assignUserRole(@Valid @RequestBody UserRoleAssignRequest userRoleAssignRequest) {
    userService.assignUserRole(userRoleAssignRequest);
  }
}
