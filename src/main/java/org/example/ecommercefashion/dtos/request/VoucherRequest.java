package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRequest {
    @NotNull(message = "ID discount không được để trống")
    private Long discountId;
}
