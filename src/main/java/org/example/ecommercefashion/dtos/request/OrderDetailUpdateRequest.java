package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailUpdateRequest {
    @NotNull(message = "Vui lòng chọn sản phẩm")
    private Long orderDetailId;
    @NotNull(message = "Vui lòng nhập số lượng")
    private Integer quantity;

}
