package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.VoucherResponse;
import org.example.ecommercefashion.entities.Voucher;
import org.springframework.data.domain.Pageable;

public interface VoucherServise {
    ResponsePage<Voucher, VoucherResponse> getAll(Pageable pageable);
}
