package org.example.ecommercefashion.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.VoucherRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.VoucherResponse;
import org.example.ecommercefashion.entities.Voucher;
import org.example.ecommercefashion.services.VoucherServise;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/voucher")
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherServise voucherServise;

    @GetMapping
    public ResponsePage<Voucher, VoucherResponse> getAll(Long id,String createAt,Long createBy,Long discountId,Pageable pageable) {
        return voucherServise.filterVoucher(id,createAt,createBy,discountId,pageable);
    }

    @PostMapping
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<VoucherResponse> add(@Valid  @RequestBody VoucherRequest request,
                                               @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(voucherServise.add(request, token));
    }

    @PutMapping("/{id}")
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<VoucherResponse> update(@PathVariable long id,@Valid @RequestBody VoucherRequest request,
                                                  @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(voucherServise.update(request, id, token));
    }

    @GetMapping("/{id}")
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<VoucherResponse> getFindById(@PathVariable Long id) {
        VoucherResponse response = voucherServise.getByVoucherId(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return null;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> getDeleted(@PathVariable Long id) {
        MessageResponse messageResponse = voucherServise.deleted(id);
        return ResponseEntity.ok(messageResponse);
    }

}
