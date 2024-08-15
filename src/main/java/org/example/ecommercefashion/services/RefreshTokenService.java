package org.example.ecommercefashion.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.example.ecommercefashion.dtos.response.AuthResponse;
import org.example.ecommercefashion.entities.RefreshToken;
import org.example.ecommercefashion.entities.User;

public interface RefreshTokenService {

  void saveUserToken(User user, String token);

  RefreshToken findRefreshToken(String token);

  void revokeAllUserToken(User user);

  AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException;
}
