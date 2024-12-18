package org.example.ecommercefashion.strategies;

import org.example.ecommercefashion.dtos.response.OrderResponse;
import org.example.ecommercefashion.entities.Order;
import org.quartz.JobExecutionException;

public interface TransactionStrategy {
    String processPayment(OrderResponse order) throws JobExecutionException;
    Order confirmPayment(TransactionDTO dto);
}
