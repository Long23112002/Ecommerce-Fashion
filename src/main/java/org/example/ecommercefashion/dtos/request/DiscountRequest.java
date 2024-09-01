package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.model.Condition;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequest {

    private Condition condition;

    private TypeDiscount type;

    private Double value;

    private Double maxValue;

    private Timestamp startDate;

    private Timestamp endDate;

    private StatusDiscount discountStatus;

    private Timestamp createAt;

    private Timestamp updateAt;

    private String createBy;

    private String updateBy;

    private Boolean deleted;

    private User user;
    public Discount toEntity(Discount discount){
        discount.setCondition(this.condition);
        discount.setType(this.getType());
        discount.setValue(this.getValue());
        discount.setMaxValue(this.getMaxValue());
        discount.setStartDate(this.getStartDate());
        discount.setEndDate(this.getEndDate());
        discount.setDiscountStatus(this.getDiscountStatus());
//        discount.setCreateAt(this.getCreateAt());
//        discount.setUpdateAt(this.getUpdateAt());
//        discount.setCreateBy();
//        discount.setUpdateBy();
        discount.setDeleted(this.getDeleted() != null ? this.getDeleted() : false);
        return discount;
    }
}
