package org.example.ecommercefashion.entities.value;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailValue {
  @NotNull private Long productDetailId;
  @NotNull private Integer quantity;
}
