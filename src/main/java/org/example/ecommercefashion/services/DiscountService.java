package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.DiscountParam;
import org.example.ecommercefashion.dtos.request.DiscountRequest;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.ProductDetail;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiscountService {
    ResponsePage<Discount, DiscountResponse> filterDiscount(DiscountParam param, Pageable pageable);

    DiscountResponse add(DiscountRequest request, String token);

    DiscountResponse getByDiscountId(Long id);

    DiscountResponse update(DiscountRequest request, Long id, String token);

    MessageResponse deleted(Long id);

    List<Discount> getVoucher(List<ProductDetail> detailList, Order order);
}
