package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Discount;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiscountService {

    ResponsePage<Discount, DiscountResponse> getAll(Pageable pageable);
}
