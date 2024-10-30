package org.example.ecommercefashion.dtos.filter;

import lombok.Data;

@Data
public class ProductParam {
    private String keyword;
    private Long idBrand;
    private Long idOrigin;
    private Long idCategory;
    private Long idMaterial;

}
