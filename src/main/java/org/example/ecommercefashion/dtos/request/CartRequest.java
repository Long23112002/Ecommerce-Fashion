package org.example.ecommercefashion.dtos.request;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.value.CartValue;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartRequest {

  private Long userId;

  private Set<CartValue> cartValues;
}