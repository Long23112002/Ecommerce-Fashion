package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.repositories.OrderDetailRepository;
import org.example.ecommercefashion.services.OrderDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository repository;
    @Override
    public Page<OrderDetail> filter( Long orderId, Pageable pageable) {
        return repository.filter(orderId, pageable);
    }
}
