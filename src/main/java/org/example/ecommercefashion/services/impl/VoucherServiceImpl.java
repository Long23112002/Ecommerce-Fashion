package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.Param.VoucherParam;
import org.example.ecommercefashion.dtos.request.VoucherRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.dtos.response.VoucherResponse;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.entities.Voucher;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.DiscountRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.repositories.VoucherRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.VoucherServise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherServise {
    private final UserRepository userRepository;

    private final DiscountRepository discountRepository;

    private final VoucherRepository voucherRepository;

    private final JwtService JwtService;

    @Override
    public ResponsePage<Voucher, VoucherResponse> filterVoucher(VoucherParam param, Pageable pageable) {
        Page<Voucher> voucherResponsesPage = voucherRepository.getFilterVoucherPage(param,pageable);
        return new ResponsePage<>(voucherResponsesPage, VoucherResponse.class);
    }

    @Override
    public VoucherResponse add(VoucherRequest request, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Voucher voucher = new Voucher();
            FnCommon.copyNonNullProperties(voucher, request);
            voucher.setCode(UUID.randomUUID());

            Discount discount = discountRepository.findById(request.getDiscountId()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.DISCOUNT_NOT_FOUND));

            voucher.setDiscount(discount);
            voucher.setCreateBy(jwt.getUserId());
            voucher = voucherRepository.save(voucher);
            VoucherResponse voucherResponse = new VoucherResponse();
            FnCommon.copyNonNullProperties(voucherResponse, voucher);
            return voucherResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }


    private UserResponse getInfoUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND)
        );
        UserResponse userResponse = new UserResponse();
        FnCommon.copyNonNullProperties(userResponse, user);
        return userResponse;

    }

    @Override
    public VoucherResponse getByVoucherId(Long id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.VOUCHER_NOT_FOUND)
        );
        VoucherResponse response = new VoucherResponse();
        FnCommon.copyNonNullProperties(response, voucher);
        if (voucher.getCreateBy() != null) {
            response.setCreateBy(getInfoUser(voucher.getCreateBy()));
        }
        if (voucher.getUpdateBy() != null) {
            response.setUpdateBy(getInfoUser(voucher.getUpdateBy()));
        }
        return response;
    }

    @Override
    public VoucherResponse update(VoucherRequest request, Long id, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Voucher voucher = voucherRepository.findById(id).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.VOUCHER_NOT_FOUND)
            );

            voucher.setUpdateBy(jwt.getUserId());
            FnCommon.copyNonNullProperties(voucher, request);

            if (request.getDiscountId() != null) {
                Discount discount = discountRepository.findById(request.getDiscountId()).orElseThrow(
                        () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.DISCOUNT_NOT_FOUND)
                );
                voucher.setDiscount(discount);
            }
            voucher = voucherRepository.save(voucher);

            VoucherResponse response = new VoucherResponse();
            FnCommon.copyNonNullProperties(response, voucher);

            return response;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    public MessageResponse deleted(Long id) {
        Voucher voucher = voucherRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.VOUCHER_NOT_FOUND)
        );
        voucher.setDeleted(true);
        voucherRepository.save(voucher);

        return MessageResponse.builder().message("Voucher deleted successfully").build();
    }
}
