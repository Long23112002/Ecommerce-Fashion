package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.VoucherResponse;
import org.example.ecommercefashion.entities.Voucher;
import org.example.ecommercefashion.repositories.VoucherRepository;
import org.example.ecommercefashion.services.VoucherServise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherServise {
    private final VoucherRepository voucherRepository;
    @Override
    public ResponsePage<Voucher, VoucherResponse>getAll(Pageable pageable){
        Page<VoucherResponse> voucherResponsesPage = voucherRepository.getAll(pageable);
        return new ResponsePage(voucherResponsesPage,VoucherResponse.class);
    }
}
