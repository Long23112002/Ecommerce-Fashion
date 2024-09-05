package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.response.AuthResponse;
import org.example.ecommercefashion.dtos.response.LoginResponse;

public interface Oauth2Service {

  LoginResponse authenticateFacebookUser(String code);

  LoginResponse authenticateGoogleUser(String code);
}
