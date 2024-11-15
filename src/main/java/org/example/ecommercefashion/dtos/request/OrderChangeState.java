package org.example.ecommercefashion.dtos.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.value.Address;
import org.example.ecommercefashion.enums.OrderStatus;
import org.example.ecommercefashion.enums.PaymentMethodEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderChangeState {
  @NotNull private OrderStatus status;
  private Address address;
  private String phoneNumber;
  private String note;
  private PaymentMethodEnum paymentMethod;
  private Double moneyShip;
  private Double totalMoney;
}
