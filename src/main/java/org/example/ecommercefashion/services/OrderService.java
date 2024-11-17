package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.OrderAddressUpdate;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderUpdateRequest;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.strategies.TransactionRequest;
import org.quartz.JobExecutionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;

public interface OrderService {


  Order createOrder(OrderCreateRequest dto, String token);

  Order updateAddress(Long id, OrderAddressUpdate dto);

  String orderUpdateAndPay(Long id, OrderUpdateRequest dto) throws UnsupportedEncodingException, JobExecutionException;

  Order updateStateOrder(Long id, OrderChangeState dto);

  Order confirmOrder(TransactionRequest request) throws JobExecutionException;

  void deleteOrder(Long id);

  Order getOrderById(Long id);

  Page<Order> filter(OrderParam param, Pageable pageable);
}
