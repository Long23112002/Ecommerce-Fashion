package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.annotations.EnumPattern;
import org.example.ecommercefashion.enums.promotion.StatusPromotionEnum;
import org.example.ecommercefashion.enums.promotion.TypePromotionEnum;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRequest {

    @NotNull(message = "Ngày bắt đầu không được trống")
    private Timestamp startDate;

    @NotNull(message = "Ngày kết thúc không được trống")
    private Timestamp endDate;

    @NotNull(message = "Kiểu khuyến mãi không được trống")
    @EnumPattern(name = "type", regexp = "PERCENTAGE_DISCOUNT|AMOUNT_DISCOUNT")
    private TypePromotionEnum typePromotionEnum;

    @NotNull(message = "Giá trị khuyến mãi không được trống")
    private Double value;

    @NotNull(message = "Trạng thái khuyến mãi không được trống")
    @EnumPattern(name = "status", regexp = "UPCOMING|ACTIVE|ENDED")
    private StatusPromotionEnum statusPromotionEnum;

}
