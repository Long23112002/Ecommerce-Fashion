package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.response.AuthResponse;

public interface Oauth2Service {

  AuthResponse authenticateFacebookUser(String code);

  AuthResponse authenticateGoogleUser(String code);
}
