package org.example.ecommercefashion.dtos.filter;

import lombok.Data;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;

import java.util.List;

@Data
public class DiscountParam {
    private TypeDiscount type;
    private  StatusDiscount status;
    private String name = "";
    private List<Long> idProductDetails;
    private Double prices;
}
