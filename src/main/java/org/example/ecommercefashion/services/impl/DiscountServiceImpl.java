package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.repositories.DiscountRepository;
import org.example.ecommercefashion.services.DiscountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    @Override
    public ResponsePage<Discount, DiscountResponse> getAll(Pageable pageable){
        Page<DiscountResponse> discountResponsesPage = discountRepository.getAll(pageable);
        return new ResponsePage(discountResponsesPage,DiscountResponse.class);
    }
}
