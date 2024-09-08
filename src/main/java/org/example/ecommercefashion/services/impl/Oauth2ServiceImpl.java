package org.example.ecommercefashion.services.impl;

import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.request.FacebookLoginRequest;
import org.example.ecommercefashion.dtos.request.GoogleLoginRequest;
import org.example.ecommercefashion.dtos.response.AuthResponse;
import org.example.ecommercefashion.dtos.response.LoginResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.GenderEnum;
import org.example.ecommercefashion.httpclient.FacebookIdentityClient;
import org.example.ecommercefashion.httpclient.FacebookUserClient;
import org.example.ecommercefashion.httpclient.GoogleIdentityClient;
import org.example.ecommercefashion.httpclient.GoogleUserClient;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.Oauth2Service;
import org.example.ecommercefashion.services.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Oauth2ServiceImpl implements Oauth2Service {

  private final UserRepository userRepository;

  private final FacebookIdentityClient facebookIdentityClient;

  private final FacebookUserClient facebookUserClient;

  private final GoogleIdentityClient googleIdentityClient;

  private final GoogleUserClient googleUserClient;

  private final JwtService jwtService;

  private final RefreshTokenService refreshTokenService;

  @NonFinal
  @Value("${facebook.client-id}")
  protected String CLIENT_ID;

  @NonFinal
  @Value("${facebook.client-secret}")
  protected String CLIENT_SECRET;

  @NonFinal
  @Value("${facebook.redirect-uri}")
  protected String REDIRECT_URI;

  @NonFinal
  @Value("${google.client-id}")
  protected String CLIENT_GOOGLE_ID;

  @NonFinal
  @Value("${google.client-secret}")
  protected String CLIENT_GOOGLE_SECRET;

  @NonFinal
  @Value("${google.redirect-uri}")
  protected String REDIRECT_GOOGLE_URI;

  @NonFinal protected final String TYPE = "authorization_code";

  @Override
  public LoginResponse authenticateFacebookUser(String code) {
    var response =
        facebookIdentityClient.exchangeToken(
            FacebookLoginRequest.builder()
                .code(code)
                .client_id(CLIENT_ID)
                .client_secret(CLIENT_SECRET)
                .redirect_uri(REDIRECT_URI)
                .build());

    log.info("TOKEN RESPONSE {}", response);

    var userInfo =
        facebookUserClient.getUserInfo("id,name,email,picture", response.getAccess_token());

    User existingUser = checkUserExist(userInfo.getEmail());

    if (existingUser == null) {
      User user = new User();
      user.setEmail(userInfo.getEmail());
      user.setGender(GenderEnum.OTHER);
      user.setFullName(userInfo.getName());
      user.setAvatar(userInfo.getPicture().getData().getUrl());
      user.setSlugFullName(userInfo.getName());
      user.setSlugEmail(userInfo.getEmail());
      userRepository.save(user);
      jwtService.generateToken(user);
      refreshTokenService.revokeAllUserToken(user);
      refreshTokenService.saveUserToken(user, jwtService.generateRefreshToken(user));
      return LoginResponse.builder()
          .authResponse(
              AuthResponse.builder()
                  .accessToken(jwtService.generateToken(user))
                  .refreshToken(jwtService.generateRefreshToken(user))
                  .build())
          .userResponse(mapToUserResponse(user))
          .build();

    } else {
      existingUser.setDeleted(false);
      existingUser.setFacebookAccountId(userInfo.getId());
      existingUser.setAvatar(userInfo.getPicture().getData().getUrl());
      refreshTokenService.revokeAllUserToken(existingUser);
      refreshTokenService.saveUserToken(
          existingUser, jwtService.generateRefreshToken(existingUser));
      userRepository.save(existingUser);
      return LoginResponse.builder()
          .authResponse(
              AuthResponse.builder()
                  .accessToken(jwtService.generateToken(existingUser))
                  .refreshToken(jwtService.generateRefreshToken(existingUser))
                  .build())
          .userResponse(mapToUserResponse(existingUser))
          .build();
    }
  }

  @Override
  public LoginResponse authenticateGoogleUser(String code) {
    var response =
        googleIdentityClient.exchangeToken(
            GoogleLoginRequest.builder()
                .code(code)
                .clientId(CLIENT_GOOGLE_ID)
                .clientSecret(CLIENT_GOOGLE_SECRET)
                .redirectUri(REDIRECT_GOOGLE_URI)
                .grantType(TYPE)
                .build());

    var userInfo = googleUserClient.getUserInfo("json", response.getAccessToken());

    User existingUser = checkUserExist(userInfo.getEmail());
    if (existingUser == null) {
      User user = new User();
      user.setEmail(userInfo.getEmail());
      user.setFullName(userInfo.getName());
      user.setGender(GenderEnum.OTHER);
      user.setAvatar(userInfo.getPicture());
      user.setSlugFullName(userInfo.getName());
      user.setSlugEmail(userInfo.getEmail());
      //      refreshTokenService.revokeAllUserToken(user);
      //      refreshTokenService.saveUserToken(user, jwtService.generateRefreshToken(user));
      userRepository.save(user);
      return LoginResponse.builder()
          .authResponse(
              AuthResponse.builder()
                  .accessToken(jwtService.generateToken(user))
                  .refreshToken(jwtService.generateRefreshToken(user))
                  .build())
          .userResponse(mapToUserResponse(user))
          .build();

    } else {
      existingUser.setGoogleAccountId(userInfo.getId());
      existingUser.setAvatar(userInfo.getPicture());
      existingUser.setDeleted(false);
      refreshTokenService.revokeAllUserToken(existingUser);
      refreshTokenService.saveUserToken(
          existingUser, jwtService.generateRefreshToken(existingUser));
      userRepository.save(existingUser);
      return LoginResponse.builder()
          .authResponse(
              AuthResponse.builder()
                  .accessToken(jwtService.generateToken(existingUser))
                  .refreshToken(jwtService.generateRefreshToken(existingUser))
                  .build())
          .userResponse(mapToUserResponse(existingUser))
          .build();
    }
  }

  private User checkUserExist(String email) {
    return userRepository.findByEmail(email);
  }

  private UserResponse mapToUserResponse(User user) {
    UserResponse userResponse = new UserResponse();
    FnCommon.copyNonNullProperties(userResponse, user);
    userResponse.setIsAdmin(user.getIsAdmin());
    return userResponse;
  }
}
