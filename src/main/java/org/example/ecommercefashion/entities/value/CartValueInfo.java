package org.example.ecommercefashion.entities.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.dtos.response.ProductDetailCartResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartValueInfo {

  private Integer quantity;

  private ProductDetailCartResponse productDetail;
}
