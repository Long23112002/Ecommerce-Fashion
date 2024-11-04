package org.example.ecommercefashion.entities.value;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartValue {

  private Integer quantity;

  private Long productDetailId;
}
