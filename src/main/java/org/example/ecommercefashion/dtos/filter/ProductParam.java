package org.example.ecommercefashion.dtos.filter;

import java.util.Collection;
import lombok.Data;

@Data
public class ProductParam {
  private String keyword;
  private String code;
  private Long idBrand;
  private Long idOrigin;
  private Long idCategory;
  private Long idMaterial;
  private Collection<Long> idColors;
  private Collection<Long> idSizes;
  private Long minPrice;
  private Long maxPrice;
  private boolean swallowEmpty;
}
