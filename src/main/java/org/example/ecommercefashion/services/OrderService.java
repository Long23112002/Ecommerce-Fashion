package org.example.ecommercefashion.services;

import java.io.UnsupportedEncodingException;
import java.util.List;
import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.OrderAddressUpdate;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderUpdateRequest;
import org.example.ecommercefashion.dtos.response.OrderResponse;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.strategies.TransactionRequest;
import org.quartz.JobExecutionException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

  OrderResponse createOrder(OrderCreateRequest dto, String token);

  OrderResponse updateAddress(Long id, OrderAddressUpdate dto);

  String orderUpdateAndPay(Long id, OrderUpdateRequest dto)
      throws UnsupportedEncodingException, JobExecutionException;

  OrderResponse updateDiscount(Long id, Long discountId);

  OrderResponse updateStateOrder(Long id, OrderChangeState dto);

  OrderResponse confirmOrder(TransactionRequest request) throws JobExecutionException;

  void deleteOrder(Long id);

  OrderResponse getOrderById(Long id);

  Page<OrderResponse> filter(OrderParam param, Pageable pageable);

  Order createOrderAtStore(String token);

  List<OrderResponse> getOrderPendingAtStore(String token);

  byte[] generateOrderPdf(Long orderId);
}
