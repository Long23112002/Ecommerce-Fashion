package org.example.ecommercefashion.services;

import org.example.ecommercefashion.entities.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderDetailService {
    Page<OrderDetail> filter(Long orderId,Pageable pageable);
}
