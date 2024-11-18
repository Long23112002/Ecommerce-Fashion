package org.example.ecommercefashion.strategies;

import org.example.ecommercefashion.entities.Order;
import org.quartz.JobExecutionException;

public interface TransactionStrategy {
    String processPayment(Order order) throws JobExecutionException;
    Order confirmPayment(TransactionDTO dto);
}
