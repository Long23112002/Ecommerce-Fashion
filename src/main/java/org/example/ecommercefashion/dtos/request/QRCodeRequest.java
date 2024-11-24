package org.example.ecommercefashion.dtos.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QRCodeRequest {
  @NotNull private Long productDetailId;

  private Long qty;
}
