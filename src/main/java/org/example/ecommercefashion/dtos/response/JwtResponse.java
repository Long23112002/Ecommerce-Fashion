package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

  private List<String> authoritiesSystem;
  private Long userId;
  private String sub;
}
