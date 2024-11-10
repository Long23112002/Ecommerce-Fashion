package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.ChangePasswordRequest;
import org.example.ecommercefashion.dtos.request.OtpRequest;
import org.example.ecommercefashion.dtos.request.UserInfoUpdateRequest;
import org.example.ecommercefashion.dtos.request.UserRequest;
import org.example.ecommercefashion.dtos.request.UserRoleAssignRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.User;
import org.quartz.JobExecutionException;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public interface UserService {

  UserResponse createUser(UserRequest userRequest) throws JobExecutionException;

  UserResponse updateUser(Long id, UserRequest userRequest);

  UserResponse updateUser(Long id, UserInfoUpdateRequest userUpdateRequest, String token);

  MessageResponse deleteUser(Long id);

  UserResponse getUserById(Long id);

  User findUserOrDefault(Long id);

  User getDeletedUser();

  MessageResponse assignRoleAdmin(String email);

  MessageResponse changePassword(ChangePasswordRequest changePasswordRequest);

  ResponsePage<User, UserResponse> getAllUsers(UserParam userParam,Pageable pageable);

  MessageResponse assignUserRole(UserRoleAssignRequest userRoleAssignRequest);

  void validEmail(OtpRequest otpRequest);

  void sendOtp(String email) throws JobExecutionException;

  List<User> findAllEntityUserByIds(Collection<Long> ids);
}
