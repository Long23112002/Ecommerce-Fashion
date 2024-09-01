package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.model.Condition;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountResponse {
    private Long id;
    private UUID code;
    private Condition condition;
    private TypeDiscount type;
    private Float value;
    private Float maxValue;
    private Timestamp startDate;
    private Timestamp endDate;
    private StatusDiscount discountStatus;
    private Timestamp createAt;
    private Timestamp updateAt;
    private String createBy;
    private String updateBy;
    private Boolean deleted;
}
