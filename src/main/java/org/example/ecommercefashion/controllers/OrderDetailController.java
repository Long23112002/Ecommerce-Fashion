package org.example.ecommercefashion.controllers;

import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.services.OrderDetailService;
import org.example.ecommercefashion.utils.ResponsePageV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order-detail")
public class OrderDetailController {
    @Autowired
    private OrderDetailService service;
    @GetMapping("/{orderId}")
    public ResponsePageV2<OrderDetail> filter(PageableRequest pageableRequest, @PathVariable Long orderId) {
        return new ResponsePageV2<>(service.filter(orderId, pageableRequest.toPageable()));
    }
}
