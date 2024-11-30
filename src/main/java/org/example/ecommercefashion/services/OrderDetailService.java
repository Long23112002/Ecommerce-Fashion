package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.OrderDetailCreateRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDetailService {
    Page<OrderDetail> filter(Long orderId,Pageable pageable);

    OrderDetail addProductDetailToOrderDetail(OrderDetailCreateRequest request, String token);
    Order deleteOrderDetail(Long id);
}
