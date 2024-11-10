package org.example.ecommercefashion.dtos.filter;

import java.util.List;
import lombok.Data;

@Data
public class ProductParam {
  private String keyword;
  private String code;
  private Long idBrand;
  private Long idOrigin;
  private Long idCategory;
  private Long idMaterial;
  private List<Long> idColors;
  private List<Long> idSizes;
  private Double minPrice;
  private Double maxPrice;
}
