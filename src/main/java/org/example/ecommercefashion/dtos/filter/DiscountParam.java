package org.example.ecommercefashion.dtos.filter;

import lombok.Data;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiscountParam {
    TypeDiscount type;
    StatusDiscount status;
    String name;
    List<Long> idProductDetail;
    Double prices;

    public List<Long> getDefaultList() {
        return new ArrayList<>();
    }
}