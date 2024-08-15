package org.example.ecommercefashion.dtos.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthResponse {

  private String accessToken;

  private String refreshToken;
}
