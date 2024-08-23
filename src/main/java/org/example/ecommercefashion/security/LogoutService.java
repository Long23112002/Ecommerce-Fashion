package org.example.ecommercefashion.security;

import com.longnh.exceptions.ExceptionHandle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public void logout(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    final String authHeader = request.getHeader("Authorization");
    final String jwtRefreshToken;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    jwtRefreshToken = authHeader.substring(7);

    var refreshToken =
        Optional.ofNullable(refreshTokenRepository.findByToken(jwtRefreshToken))
            .orElseThrow(
                () ->
                    new ExceptionHandle(
                        HttpStatus.NOT_FOUND, ErrorMessage.REFRESH_TOKEN_NOT_FOUND));

    refreshToken.setRevoked(true);
    refreshTokenRepository.save(refreshToken);
  }
}
