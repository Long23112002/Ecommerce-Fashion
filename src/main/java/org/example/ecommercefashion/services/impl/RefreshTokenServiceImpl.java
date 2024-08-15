package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.AuthResponse;
import org.example.ecommercefashion.entities.RefreshToken;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.RefreshTokenRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  private final JwtService jwtService;

  private final UserRepository userRepository;

  @Override
  public void saveUserToken(User user, String token) {
    var refreshToken = RefreshToken.builder().revoked(false).token(token).user(user).build();

    refreshTokenRepository.save(refreshToken);
  }

  @Override
  public RefreshToken findRefreshToken(String token) {
    return Optional.ofNullable(refreshTokenRepository.findByToken(token))
        .orElseThrow(
            () ->
                new ExceptionHandle(
                    HttpStatus.NOT_FOUND, ErrorMessage.REFRESH_TOKEN_NOT_FOUND.val()));
  }

  public void revokeAllUserToken(User user) {
    var validUserTokens = refreshTokenRepository.findAllValidTokenByUserId(user.getId());
    if (validUserTokens.isEmpty()) return;
    validUserTokens.forEach(token -> token.setRevoked(true));
    refreshTokenRepository.saveAll(validUserTokens);
  }

  @Override
  public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
    final String authHeader = request.getHeader("Authorization");
    final String refreshToken;
    final String email;
    final String refreshTokenKey = jwtService.getJwtRefreshKey();

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN.val());
    }

    refreshToken = authHeader.substring(7);
    email = jwtService.extractUserName(refreshToken, refreshTokenKey);
    User user =
        Optional.ofNullable(userRepository.findByEmail(email))
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val()));

    if (!user.getEmail().equals(email)) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN.val());
    }

    RefreshToken existRefreshToken =
        Optional.ofNullable(refreshTokenRepository.findByToken(refreshToken))
            .orElseThrow(
                () ->
                    new ExceptionHandle(
                        HttpStatus.NOT_FOUND, ErrorMessage.REFRESH_TOKEN_NOT_FOUND.val()));

    if (!jwtService.isTokenValid(existRefreshToken.getToken(), user, refreshTokenKey)) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN.val());
    }

    String newAccessToken = jwtService.generateToken(user);
    String newRefreshToken =
        jwtService.generateNewRefreshTokenWithOldExpiryTime(existRefreshToken.getToken(), user);

    revokeAllUserToken(user);
    saveUserToken(user, newRefreshToken);
    return AuthResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build();
  }
}
