package org.example.ecommercefashion.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class DiscountResponse {
    private Long id;
    private UUID code;
    private String name;
    private Condition condition;
    private TypeDiscount type;
    private Double value;
    private Double maxValue;
    private Timestamp startDate;
    private Timestamp endDate;
    private StatusDiscount discountStatus;
    private Timestamp createAt;
    private Timestamp updateAt;
    private UserResponse createBy;
    private UserResponse updateBy;
    private Boolean deleted;
}
