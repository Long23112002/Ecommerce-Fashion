package org.example.ecommercefashion.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.FacebookLoginRequest;
import org.example.ecommercefashion.dtos.request.LoginRequest;
import org.example.ecommercefashion.dtos.request.ResetPasswordRequest;
import org.example.ecommercefashion.dtos.request.UserRequest;
import org.example.ecommercefashion.dtos.response.AuthResponse;
import org.example.ecommercefashion.dtos.response.LoginResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.services.AuthenticationService;
import org.example.ecommercefashion.services.Oauth2Service;
import org.example.ecommercefashion.services.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthenticationService authenticationService;

  private final RefreshTokenService refreshTokenService;

  private final Oauth2Service oauth2Service;

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
    return authenticationService.login(loginRequest);
  }

  @PostMapping("/signup")
  public UserResponse signUp(@Valid @RequestBody UserRequest userRequest) {
    return authenticationService.signUp(userRequest);
  }

  @PostMapping("/reset-password")
  public MessageResponse resetPassword(
      @Valid @RequestBody ResetPasswordRequest resetPasswordRequest, String token) {
    return authenticationService.resetPassword(resetPasswordRequest, token);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthResponse> refreshToken(
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    AuthResponse res = refreshTokenService.refreshToken(request, response);
    return ResponseEntity.ok(res);
  }

  @PostMapping("/facebook-login")
  public AuthResponse facebookLogin(@RequestBody FacebookLoginRequest facebookLoginRequest) {
    return oauth2Service.authenticateFacebookUser(facebookLoginRequest.getCode());
  }

  @PostMapping("/google-login")
  public AuthResponse googleLogin(@RequestParam("code") String code) {
    return oauth2Service.authenticateGoogleUser(code);
  }
}
