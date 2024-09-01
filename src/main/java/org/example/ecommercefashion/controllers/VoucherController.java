package org.example.ecommercefashion.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.VoucherResponse;
import org.example.ecommercefashion.entities.Voucher;
import org.example.ecommercefashion.services.VoucherServise;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/voucher")
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherServise voucherServise;
    @GetMapping
    public ResponsePage<Voucher, VoucherResponse> getAll(Pageable pageable){
        return voucherServise.getAll(pageable);
    }
}
