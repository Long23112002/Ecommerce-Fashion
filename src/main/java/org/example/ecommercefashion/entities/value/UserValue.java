package org.example.ecommercefashion.entities.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.dtos.response.RoleResponse;
import org.example.ecommercefashion.enums.GenderEnum;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserValue {
    private Long id;

    private String email;

    private String fullName;

    private String phoneNumber;

    private Date birth;

    private GenderEnum gender;

    private String avatar;

    private Boolean isAdmin;

    private Set<RoleResponse> roles;
}
