package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.dtos.request.ChangePasswordRequest;
import org.example.ecommercefashion.dtos.request.OtpRequest;
import org.example.ecommercefashion.dtos.request.UserRequest;
import org.example.ecommercefashion.dtos.request.UserRoleAssignRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.User;
import org.quartz.JobExecutionException;
import org.springframework.data.domain.Pageable;

@SuppressWarnings("unused")
public interface UserService {

  UserResponse createUser(UserRequest userRequest) throws JobExecutionException;

  UserResponse updateUser(Long id, UserRequest userRequest);

  MessageResponse deleteUser(Long id);

  UserResponse getUserById(Long id);

  MessageResponse assignRoleAdmin(String email);

  MessageResponse changePassword(ChangePasswordRequest changePasswordRequest);

  ResponsePage<User, UserResponse> getAllUsers(UserParam userParam,Pageable pageable);

  MessageResponse assignUserRole(UserRoleAssignRequest userRoleAssignRequest);

  void validEmail(OtpRequest otpRequest);

  void sendOtp(String email) throws JobExecutionException;
}
