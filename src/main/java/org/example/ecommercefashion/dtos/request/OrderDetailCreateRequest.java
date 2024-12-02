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
public class OrderDetailCreateRequest {
    @NotNull(message = "Bạn chưa chọn hóa đơn")
    private Long idOrder;
    @NotNull(message = "Bạn chưa chọn sản phẩm")
    private Long idProductDetail;
    @NotNull(message = "Vui lòng nhập số lượng")
    private Integer quantity;

}
