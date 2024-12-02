package org.example.ecommercefashion.strategies;

import org.example.ecommercefashion.dtos.response.OrderResponse;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.enums.PaymentMethodEnum;
import org.example.ecommercefashion.services.OrderService;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("vietqr")
public class VietQrTransaction implements TransactionStrategy{

    @Autowired
    private OrderService orderService;

    @Override
    public String processPayment(OrderResponse order) throws JobExecutionException {
        TransactionRequest request = TransactionRequest.builder()
                .orderId(order.getId())
                .paymentMethod(PaymentMethodEnum.VIET_QR)
                .build();
        orderService.confirmOrder(request);
        return null;
    }

    @Override
    public Order confirmPayment(TransactionDTO dto) {
        Order order = dto.getOrder();
        order.setPaymentMethod(PaymentMethodEnum.VIET_QR);
        return order;
    }
}
