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
    @NotNull(message = "Voucher code cannot be null")
    private UUID code;
    @NotNull(message = "Creation time cannot be null")
    @PastOrPresent(message = "Creation time must be in the past or present")
    private Timestamp createAt;
    @UpdateTimestamp
    private Timestamp updateAt;

    private Timestamp usedAt;
    @NotNull(message = "Created by cannot be null")
    private Long createBy;

    private Long updateBy;

    private Long usedBy;
    @NotNull(message = "Deleted status cannot be null")
    private Boolean deleted;
    @NotNull(message = "ID giảm giá không được để trống")
    private Long discountId;
}
