package org.example.ecommercefashion.dtos.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GoogleUserResponse {
  private String id;

  private String email;

  private boolean verifiedEmail;

  private String name;

  private String givenName;

  private String familyName;

  private String picture;

  private String locale;
}
