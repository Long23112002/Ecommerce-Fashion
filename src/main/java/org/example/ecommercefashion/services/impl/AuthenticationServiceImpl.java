package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.LoginRequest;
import org.example.ecommercefashion.dtos.request.ResetPasswordRequest;
import org.example.ecommercefashion.dtos.request.UserRequest;
import org.example.ecommercefashion.dtos.response.AuthResponse;
import org.example.ecommercefashion.dtos.response.LoginResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.AuthenticationService;
import org.example.ecommercefashion.services.RefreshTokenService;
import org.example.ecommercefashion.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  private final PasswordEncoder passwordEncoder;

  private final RefreshTokenService refreshTokenService;

  private final UserRepository userRepository;

  private final UserService userService;

  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    Authentication authentication;

    try {
      authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  loginRequest.getEmail(), loginRequest.getPassword()));
    } catch (AuthenticationException e) {
      throw new ExceptionHandle(HttpStatus.UNAUTHORIZED, ErrorMessage.BAD_CREDENTIAL.val());
    }

    SecurityContextHolder.getContext().setAuthentication(authentication);
    User user = (User) authentication.getPrincipal();

    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    refreshTokenService.revokeAllUserToken(user);
    refreshTokenService.saveUserToken(user, refreshToken);

    return LoginResponse.builder()
        .authResponse(
            AuthResponse.builder()
                    .refreshToken(refreshToken)
                    .accessToken(accessToken)
                    .build())
        .userResponse(
            UserResponse.builder()
                .id(user.getId())
                .birth(user.getBirth())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .avatar(user.getAvatar())
                .build())
        .build();
  }

  @Override
  @Transactional
  public UserResponse signUp(UserRequest userRequest) {
    return userService.createUser(userRequest);
  }

  @Override
  @Transactional
  public MessageResponse resetPassword(ResetPasswordRequest request, String token) {
    String email = jwtService.extractVerifyEmail(token, jwtService.getJwtKey());

    User user =
        Optional.ofNullable(userRepository.findByEmail(email))
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val()));

    String currentPassword = user.getPassword();
    if (passwordEncoder.matches(request.getNewPassword(), currentPassword)) {
      throw new ExceptionHandle(
          HttpStatus.BAD_REQUEST, ErrorMessage.CURRENT_PASSWORD_SAME_NEW_PASSWORD.val());
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    return MessageResponse.builder().message("Reset password successful").build();
  }
}
