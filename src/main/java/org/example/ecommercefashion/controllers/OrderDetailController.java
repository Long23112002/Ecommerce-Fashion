package org.example.ecommercefashion.controllers;

import org.example.ecommercefashion.dtos.request.OrderCreateRequest;
import org.example.ecommercefashion.dtos.request.OrderDetailCreateRequest;
import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.services.OrderDetailService;
import org.example.ecommercefashion.services.PaymentService;
import org.example.ecommercefashion.utils.ResponsePageV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/order-detail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService service;



    @GetMapping("/{orderId}")
    public ResponsePageV2<OrderDetail> filter(PageableRequest pageableRequest, @PathVariable Long orderId) {
        return new ResponsePageV2<>(service.filter(orderId, pageableRequest.toPageable()));
    }

    @PostMapping
    public OrderDetail create(
            @Valid @RequestBody OrderDetailCreateRequest request, @RequestHeader("Authorization") String token) {
        return service.addProductDetailToOrderDetail(request, token);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
       return ResponseEntity.ok( service.deleteOrderDetail(id));
    }




}
