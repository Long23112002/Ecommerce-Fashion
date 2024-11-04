package org.example.ecommercefashion.dtos.request;

import javax.validation.constraints.*;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.annotations.EnumPattern;
import org.example.ecommercefashion.annotations.ValidBirth;
import org.example.ecommercefashion.annotations.ValidPhoneNumber;
import org.example.ecommercefashion.enums.GenderEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoUpdateRequest {

    @NotNull(message = "Full name is required")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullName;

    @NotNull(message = "Phone number is required")
    @ValidPhoneNumber
    private String phoneNumber;

    @Past(message = "Birth date must be in the past")
    @ValidBirth
    private Date birth;

    @NotNull(message = "gender is required")
    @EnumPattern(name = "gender", regexp = "MALE|FEMALE|OTHER")
    private GenderEnum gender;

    private String avatar;
}
