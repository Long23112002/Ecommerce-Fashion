package org.example.ecommercefashion.entities.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
  private Long id;

  private String email;

  private String fullName;

  private String avatar;
}
