package org.example.ecommercefashion.dtos.Param;

import lombok.Data;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;

@Data
public class DiscountParam {
    private TypeDiscount type;
    private StatusDiscount status;
    private String name;
}
