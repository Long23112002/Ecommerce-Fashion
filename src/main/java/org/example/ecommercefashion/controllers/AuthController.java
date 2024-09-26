package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.*;
import org.example.ecommercefashion.dtos.response.*;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.AuthenticationService;
import org.example.ecommercefashion.services.Oauth2Service;
import org.example.ecommercefashion.services.RefreshTokenService;
import org.example.ecommercefashion.services.UserService;
import org.quartz.JobExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Api(tags = "Auth", value = "Endpoints for authentication")
public class AuthController {

  private final AuthenticationService authenticationService;

  private final RefreshTokenService refreshTokenService;

  private final Oauth2Service oauth2Service;

  private final JwtService jwtService;

  private final UserService userService;

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
    return authenticationService.login(loginRequest);
  }

  @PostMapping("/signup")
  public UserResponse signUp(@Valid @RequestBody UserRequest userRequest) throws JobExecutionException {
    return authenticationService.signUp(userRequest);
  }

  @PostMapping("/valid-email")
  public void validEmail(@Valid @RequestBody OtpRequest otpRequest) {
    userService.validEmail(otpRequest);
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
  public LoginResponse facebookLogin(@RequestBody FacebookLoginRequest facebookLoginRequest) {
    return oauth2Service.authenticateFacebookUser(facebookLoginRequest.getCode());
  }

  @PostMapping("/google-login")
  public LoginResponse googleLogin(@RequestParam("code") String code) {
    return oauth2Service.authenticateGoogleUser(code);
  }

  @PostMapping("/claim")
  public JwtResponse claim(@RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return jwtService.decodeToken(token);
  }
}
