package org.example.ecommercefashion.strategies;

import com.longnh.exceptions.ExceptionHandle;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.enums.PaymentMethodEnum;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.services.OrderService;
import org.example.ecommercefashion.services.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component("vnpay")
public class VnPayOrderTransaction implements TransactionStrategy {

    @Autowired
    private VNPayService vnPayService;

    @Override
    public String processPayment(Order order) {
        try {
            Long orderId = order.getId();
            long amount = order.getFinalPrice().longValue();
            return vnPayService.createPayment(amount, orderId);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported", e);
        }
    }

    @Override
    public Order confirmPayment(TransactionDTO dto) {
        Order order = dto.getOrder();
        if (!dto.getStatus().equals("00")) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PAYMENT_FAILED);
        }
        if (!vnPayService.match(order, dto.getConfirmationCode())) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.SECURE_NOT_MATCH);
        }
        order.setPaymentMethod(PaymentMethodEnum.VNPAY);
        return order;
    }
}
