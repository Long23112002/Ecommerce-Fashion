package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.services.DiscountService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/discount")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;
    @GetMapping
    public ResponsePage<Discount, DiscountResponse> getAll(Pageable pageable){
        return discountService.getAll(pageable);
    }
}
