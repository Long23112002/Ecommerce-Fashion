package org.example.ecommercefashion.controllers;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.dtos.request.OrderAddressUpdate;
import org.example.ecommercefashion.dtos.request.OrderAtStoreCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderChangeState;
import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderUpdateRequest;
import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.services.OrderService;
import org.example.ecommercefashion.strategies.TransactionRequest;
import org.example.ecommercefashion.utils.ResponsePageV2;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order create(
            @Valid @RequestBody OrderCreateRequest orderRequest, @RequestHeader("Authorization") String token) {
        return orderService.createOrder(orderRequest, token);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/update-address/{id}")
    public Order updateAdress(@PathVariable Long id,
                              @Valid @RequestBody OrderAddressUpdate orderAddressUpdate) {
        return orderService.updateAddress(id, orderAddressUpdate);
    }

    @PutMapping("/update-discount/{id}")
    public Order updateDiscount(@PathVariable Long id,
                                @RequestParam Long discountId) {
        return orderService.updateDiscount(id, discountId);
    }

    @PutMapping("/payment/{id}")
    public String orderUpdateAndPay(@PathVariable Long id,
                                    @Valid @RequestBody OrderUpdateRequest orderUpdateRequest) throws UnsupportedEncodingException, JobExecutionException {
        return orderService.orderUpdateAndPay(id, orderUpdateRequest);
    }

    @PutMapping("/confirm")
    public Order orderUpdateAndPay(@RequestBody TransactionRequest request) throws JobExecutionException {
        return orderService.confirmOrder(request);
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

    @GetMapping("/store")
    public Order createOrderAtStore(@RequestHeader("Authorization") String token) {
        return orderService.createOrderAtStore(token);
    }

    @GetMapping("/list-pending")
    public List<Order> getOrderPendingAtStore(@RequestHeader("Authorization") String token) {
        return orderService.getOrderPendingAtStore(token);
    }
}



