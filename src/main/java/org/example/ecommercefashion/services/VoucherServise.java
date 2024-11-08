package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.VoucherParam;
import org.example.ecommercefashion.dtos.request.VoucherRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.VoucherResponse;
import org.example.ecommercefashion.entities.Voucher;
import org.springframework.data.domain.Pageable;

public interface VoucherServise {

    ResponsePage<Voucher, VoucherResponse> filterVoucher(VoucherParam paramgit , Pageable pageable);

    VoucherResponse add(VoucherRequest request, String token);

    VoucherResponse getByVoucherId(Long id);

    VoucherResponse update(VoucherRequest request, Long id, String token);

    MessageResponse deleted(Long id);

    Voucher getVoucherByCode(String voucherCode);
}
