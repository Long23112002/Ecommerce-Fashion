package org.example.ecommercefashion.dtos.request;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.value.CartValue;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartRequest {

  private Long userId;

  private List<CartValue> cartValues;
}
