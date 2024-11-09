package org.example.ecommercefashion.dtos.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.enums.OrderStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderChangeState {
  @NotNull private OrderStatus status;
  private String address;
  private String phoneNumber;
  private String note;
  private Long paymentMethodId;
  private Double moneyShip;
  private Double totalMoney;
}