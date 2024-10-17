package org.example.ecommercefashion.dtos.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailParam {
    private String productName;
    private String sizeName;
    private String colorName;
    private Double price;

}
