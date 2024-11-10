package org.example.ecommercefashion.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.annotations.EnumPattern;
import org.example.ecommercefashion.entities.Condition;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequest {
    @Valid
    @NotNull(message = "condition cannot be null")
    private Condition condition;

    @NotBlank(message = "name cannot be null")
    private String name;

    @NotNull(message = "type is required")
    @EnumPattern(name = "type", regexp = "PERCENTAGE|FIXED_AMOUNT")
    private TypeDiscount type;

    @NotNull(message = "Value cannot be null")
    private Double value;

    private Double maxValue;

    @NotNull(message = "Ngày bắt đầu không được trống")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp startDate;

    @NotNull(message = "Ngày kết thúc không được trống")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp endDate;

    @EnumPattern(name = "discountStatus", regexp = "UPCOMING|ACTIVE|ENDED")
    private StatusDiscount discountStatus;

}
