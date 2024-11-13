package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderRequest;
import org.example.ecommercefashion.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

  Order createOrder(OrderCreateRequest dto, String token);

  Order updateStateOrder(Long id, OrderChangeState dto);

  void deleteOrder(Long id);

  Order getOrderById(Long id);

  Page<Order> filter(OrderParam param, Pageable pageable);
}
