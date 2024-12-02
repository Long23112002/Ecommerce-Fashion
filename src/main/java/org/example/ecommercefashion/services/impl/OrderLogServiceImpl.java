package org.example.ecommercefashion.services.impl;

import org.example.ecommercefashion.entities.OrderLog;
import org.example.ecommercefashion.repositories.OrderLogRepository;
import org.example.ecommercefashion.services.OrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderLogServiceImpl implements OrderLogService {

    @Autowired
    private OrderLogRepository orderLogRepository;

    @Override
    public void create(OrderLog orderLog) {
        OrderLog orderLogs = new OrderLog();
        orderLogs.setOrder(orderLogs.getOrder());
        orderLogs.setNewValue(orderLogs.getNewValue());
        orderLogs.setOldStatus(orderLogs.getOldStatus());
        orderLogRepository.save(orderLog);
    }
}
