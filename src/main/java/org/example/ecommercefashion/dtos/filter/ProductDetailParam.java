package org.example.ecommercefashion.dtos.filter;

import lombok.Data;


@Data
public class ProductDetailParam {
    private Double minPrice;
    private Double maxPrice;
    private Long idProduct;
    private Long idColor;
    private Long idSize;
    private String keyword;
}
