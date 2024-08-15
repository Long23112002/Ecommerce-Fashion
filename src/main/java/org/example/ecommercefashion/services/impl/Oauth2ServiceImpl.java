package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.request.FacebookLoginRequest;
import org.example.ecommercefashion.dtos.request.GoogleLoginRequest;
import org.example.ecommercefashion.dtos.response.AuthResponse;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.httpclient.FacebookIdentityClient;
import org.example.ecommercefashion.httpclient.FacebookUserClient;
import org.example.ecommercefashion.httpclient.GoogleIdentityClient;
import org.example.ecommercefashion.httpclient.GoogleUserClient;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.Oauth2Service;
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
  public AuthResponse authenticateFacebookUser(String code) {
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
      user.setFullName(userInfo.getName());
      user.setAvatar(userInfo.getPicture().getData().getUrl());
      userRepository.save(user);
      jwtService.generateToken(user);
      return AuthResponse.builder()
          .accessToken(jwtService.generateToken(user))
          .refreshToken(jwtService.generateRefreshToken(user))
          .build();

    } else {
      existingUser.setDeleted(false);
      existingUser.setFacebookAccountId(userInfo.getId());
      existingUser.setAvatar(userInfo.getPicture().getData().getUrl());
      userRepository.save(existingUser);
      return AuthResponse.builder()
          .accessToken(jwtService.generateToken(existingUser))
          .refreshToken(jwtService.generateRefreshToken(existingUser))
          .build();
    }
  }

  @Override
  public AuthResponse authenticateGoogleUser(String code) {
    var response =
        googleIdentityClient.exchangeToken(
            GoogleLoginRequest.builder()
                .code(code)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .redirectUri(REDIRECT_URI)
                .grantType(TYPE)
                .build());

    var userInfo = googleUserClient.getUserInfo("json", response.getAccessToken());

    User existingUser = checkUserExist(userInfo.getEmail());
    if (existingUser == null) {
      User user = new User();
      user.setEmail(userInfo.getEmail());
      user.setFullName(userInfo.getName());
      user.setAvatar(userInfo.getPicture());
      userRepository.save(user);
      jwtService.generateToken(user);
      return AuthResponse.builder()
          .accessToken(jwtService.generateToken(user))
          .refreshToken(jwtService.generateRefreshToken(user))
          .build();
    } else {
      existingUser.setGoogleAccountId(userInfo.getId());
      existingUser.setAvatar(userInfo.getPicture());
      existingUser.setDeleted(false);
      userRepository.save(existingUser);
      return AuthResponse.builder()
          .accessToken(jwtService.generateToken(existingUser))
          .refreshToken(jwtService.generateRefreshToken(existingUser))
          .build();
    }
  }

  private User checkUserExist(String email) {
    return userRepository.findByEmail(email);
  }
}
