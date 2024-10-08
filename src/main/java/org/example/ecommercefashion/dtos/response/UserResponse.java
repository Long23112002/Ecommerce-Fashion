package org.example.ecommercefashion.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.enums.GenderEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

  private Long id;

  private String email;

  private String fullName;

  private String phoneNumber;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private Date birth;

  private GenderEnum gender;

  private String avatar;

  private Boolean isAdmin;

  private Set<RoleResponse> roles;
}
