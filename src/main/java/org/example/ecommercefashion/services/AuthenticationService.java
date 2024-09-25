package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.LoginRequest;
import org.example.ecommercefashion.dtos.request.ResetPasswordRequest;
import org.example.ecommercefashion.dtos.request.UserRequest;
import org.example.ecommercefashion.dtos.response.LoginResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.quartz.JobExecutionException;

public interface AuthenticationService {

  LoginResponse login(LoginRequest loginRequest);

  MessageResponse resetPassword(ResetPasswordRequest request, String token);

  UserResponse signUp(UserRequest userRequest) throws JobExecutionException;
}
