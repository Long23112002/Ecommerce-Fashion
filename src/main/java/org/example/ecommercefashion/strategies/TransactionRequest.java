package org.example.ecommercefashion.strategies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.enums.PaymentMethodEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    private Long orderId;
    private String confirmationCode;
    private String status;
    private PaymentMethodEnum paymentMethod;
}
