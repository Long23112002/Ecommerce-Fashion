package org.example.ecommercefashion.dtos.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailParam {
    private String keyword;
    private Double minPrice;
    private Double maxPrice;
    private Timestamp startDate;
    private Timestamp endDate;

}
