package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.annotations.ValidPassword;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ResetPasswordRequest {

    @ValidPassword
    private String newPassword;
}
