package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRequest {
    private UUID code;

    @NotNull(message = "ID discount giá không được để trống")
    private Long discountId;
}
