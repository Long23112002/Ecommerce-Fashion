package org.example.ecommercefashion.entities.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartValue {

  private Long productDetailId;

  private Integer quantity;

}
