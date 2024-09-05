package org.example.ecommercefashion.dtos.filter;

import lombok.Data;
import org.example.ecommercefashion.enums.GenderEnum;

@Data
public class UserParam {

  private String email;

  private String phone;

  private String fullName;

  private GenderEnum gender;
}
