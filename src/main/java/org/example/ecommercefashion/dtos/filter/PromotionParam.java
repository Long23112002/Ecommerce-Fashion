package org.example.ecommercefashion.dtos.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.example.ecommercefashion.enums.promotion.StatusPromotionEnum;
import org.example.ecommercefashion.enums.promotion.TypePromotionEnum;

import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = false)
public class PromotionParam {

    String startDate;

    String endDate;

    TypePromotionEnum typePromotionEnum;

    StatusPromotionEnum statusPromotionEnum;

    Double valueMin;

    Double valueMax;

}
