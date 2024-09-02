package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.annotations.EnumPattern;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.model.Condition;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequest {
    @NotNull(message = "Voucher code cannot be null")
    private UUID code;

    @Valid
    @NotNull(message = "condition cannot be null")
    private Condition condition;

    @NotNull(message = "type is required")
    @EnumPattern(name = "type", regexp = "PERCENTAGE|FIXED_AMOUNT")
    private TypeDiscount type;

    @NotNull(message = "Value cannot be null")
    private Double value;

    @NotNull(message = "maxValue cannot be null")
    private Double maxValue;

    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date must be in the past or present")
    private Timestamp startDate;

    private Timestamp endDate;

    @NotNull(message = "Status is required")
    @EnumPattern(name = "discountStatus", regexp = "ACTIVE|INACTIVE|EXPIRED")
    private StatusDiscount discountStatus;

    @NotNull(message = "Creation date cannot be null")
    @PastOrPresent(message = "Creation date must be in the past or present")
    private Timestamp createAt;

    private Timestamp updateAt;

    @NotNull(message = "Created by cannot be null")
    private Long createBy;

    private Long updateBy;

    @NotNull(message = "Deleted status cannot be null")
    private Boolean deleted;

}
