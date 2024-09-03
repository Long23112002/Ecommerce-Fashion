package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.DiscountRequest;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.DiscountRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.DiscountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final DiscountRepository discountRepository;

    @Override
    public ResponsePage<Discount, DiscountResponse> filterDiscount(String type, String discountStatus,Long id,Pageable pageable) {
        Page<Discount> discountResponsesPage = discountRepository.getFilterDiscountPage(type,discountStatus,id,pageable);
        return new ResponsePage<>(discountResponsesPage, DiscountResponse.class);
    }

    @Override
    public DiscountResponse add(DiscountRequest request, String token) {

        if (token != null) {
            JwtResponse jwt = jwtService.decodeToken(token);
            Discount discount = new Discount();
            FnCommon.copyNonNullProperties(discount, request);
            discount.setCode(UUID.randomUUID());
            discount = discountRepository.save(discount);
            DiscountResponse discountResponse = new DiscountResponse();
            FnCommon.copyNonNullProperties(discountResponse, discount);
            discountResponse.setCreateBy(getInfoUser(jwt.getUserId()));
            return discountResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public DiscountResponse getByDiscountId(Long id) {
        Discount discount = discountRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.DISCOUNT_NOT_FOUND)
        );
        DiscountResponse response = new DiscountResponse();
        FnCommon.copyNonNullProperties(response, discount);
        if (discount.getCreateBy() != null) {
            response.setCreateBy(getInfoUser(discount.getCreateBy()));
        }
        if (discount.getUpdateBy() != null) {
            response.setUpdateBy(getInfoUser(discount.getUpdateBy()));
        }
        return response;
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
    public DiscountResponse update(DiscountRequest request, Long id, String token) {
        if (token != null) {
            JwtResponse jwt = jwtService.decodeToken(token);
            //Luồng :
            // Lấy Discount hiện tại từ cơ sở dữ liệu
            Discount discount = discountRepository.findById(id).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.DISCOUNT_NOT_FOUND)
            );

            // Sao chép dữ liệu từ request vào Discount hiện tại
            FnCommon.copyNonNullProperties(discount, request);

            // Cập nhật discount trong cơ sở dữ liệu
            discount = discountRepository.save(discount);

            // Chuyển đổi Discount sang DiscountResponse
            DiscountResponse discountResponse = new DiscountResponse();
            FnCommon.copyNonNullProperties(discountResponse, discount);

            // Cập nhật thông tin người dùng
            discountResponse.setUpdateBy(getInfoUser(jwt.getUserId()));

            return discountResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN);
        }

    }

    @Override
    public MessageResponse deleted(Long id) {
        Discount discount = discountRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.DISCOUNT_NOT_FOUND)
        );

        discount.setDeleted(true);
        discountRepository.save(discount);

        return MessageResponse.builder().message("discount deleted successfully").build();
    }
}
