package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  @Override
  public void revokeAllUserToken(User user) {
    var validUserTokens = refreshTokenRepository.findAllValidTokenByUserId(user.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(token -> token.setRevoked(true));
    refreshTokenRepository.saveAll(validUserTokens);
  }

  @Override
  public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {

    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN.val());
    }

    final String refreshToken = authHeader.substring(7);
    final String refreshTokenKey = jwtService.getJwtRefreshKey();
    final String email = jwtService.extractUserName(refreshToken, refreshTokenKey);

    User user =
        Optional.ofNullable(userRepository.findByEmail(email))
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND.val()));

    List<RefreshToken> tokens = refreshTokenRepository.findTokensByToken(refreshToken);
    if (tokens.isEmpty()) {
      throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.REFRESH_TOKEN_NOT_FOUND.val());
    }

    RefreshToken existRefreshToken = tokens.get(0);

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
