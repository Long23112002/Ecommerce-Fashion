package org.example.ecommercefashion.dtos.request;

import java.sql.Timestamp;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.value.OrderDetailValue;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

  private Long discountId;

  //  @NotNull private OrderStatus status;

  @NotNull(message = "Phương thức thanh toán không được trống")
  private Long paymentMethodId;

  @NotBlank(message = "Số điện thoại không được trống")
  private String phoneNumber;

  @NotBlank(message = "Địa chỉ giao hàng không được trống")
  private String address;

  private Timestamp shipDate;

  private Double moneyShip;

  private String note;

  @NotNull private Double totalMoney;

  @Size(min = 1, message = "Đơn hàng của bạn ít nhất phải có 1 sản phẩm")
  private List<OrderDetailValue> orderDetails;
}
