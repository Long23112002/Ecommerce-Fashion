package org.example.ecommercefashion.controllers;

import javax.validation.Valid;
import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderRequest;
import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.services.OrderService;
import org.example.ecommercefashion.utils.ResponsePageV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  @Autowired private OrderService orderService;

  @PostMapping
  public Order create(
      @Valid @RequestBody OrderRequest orderRequest, @RequestHeader("Authorization") String token) {
    return orderService.createOrder(orderRequest, token);
  }

  @GetMapping("/{id}")
  public Order getOrderById(@PathVariable Long id) {
    return orderService.getOrderById(id);
  }

  @PutMapping("/{id}")
  public Order updateStateOrder(
      @PathVariable Long id, @Valid @RequestBody OrderChangeState orderChangeState) {
    return orderService.updateStateOrder(id, orderChangeState);
  }

  @DeleteMapping("/{id}")
  public void deleteOrder(@PathVariable Long id) {
    orderService.deleteOrder(id);
  }

  @GetMapping
  public ResponsePageV2<Order> filter(OrderParam param, PageableRequest pageableRequest) {
    return new ResponsePageV2<>(orderService.filter(param, pageableRequest.toPageable()));
  }
}