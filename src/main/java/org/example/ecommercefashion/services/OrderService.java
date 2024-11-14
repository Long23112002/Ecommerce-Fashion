package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.OrderAddressUpdate;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderRequest;
import org.example.ecommercefashion.dtos.request.OrderUpdateRequest;
import org.example.ecommercefashion.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;

public interface OrderService {

  Order createOrder(OrderCreateRequest dto, String token);

  Order updateAddress(Long id, OrderAddressUpdate dto);

  String orderUpdateAndPay(Long id, OrderUpdateRequest dto) throws UnsupportedEncodingException;

  Order updateStateOrder(Long id, OrderChangeState dto);

  Order confirm(Long orderId, String encode);

  void deleteOrder(Long id);

  Order getOrderById(Long id);

  Page<Order> filter(OrderParam param, Pageable pageable);
}
