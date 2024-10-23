package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.Param.DiscountParam;
import org.example.ecommercefashion.dtos.request.DiscountRequest;

import org.example.ecommercefashion.dtos.response.CategoryResponse;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.Condition;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.entities.Voucher;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.BrandRepository;
import org.example.ecommercefashion.repositories.CategoryRepository;
import org.example.ecommercefashion.repositories.DiscountRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.ProductRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.repositories.VoucherRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.DiscountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {
    private final VoucherRepository voucherRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final DiscountRepository discountRepository;

    private final ProductDetailRepository productDetailRepository;

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;
    @Override
    public ResponsePage<Discount, DiscountResponse> filterDiscount(DiscountParam param, Pageable pageable) {
        Page<Discount> discountResponsesPage = discountRepository.getFilterDiscountPage(param,pageable);
        return new ResponsePage<>(discountResponsesPage, DiscountResponse.class);
    }

    @Override
    public DiscountResponse add(DiscountRequest request, String token) {

        if (token != null) {
            JwtResponse jwt = jwtService.decodeToken(token);
            Discount discount = new Discount();
            FnCommon.copyNonNullProperties(discount, request);
            discount.setCode(UUID.randomUUID());
            discount.setCreateBy(jwt.getUserId());
            discount = discountRepository.save(discount);
            DiscountResponse discountResponse = new DiscountResponse();
            FnCommon.copyNonNullProperties(discountResponse, discount);
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
            Discount discount = discountRepository.findById(id).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.DISCOUNT_NOT_FOUND)
            );

            FnCommon.copyNonNullProperties(discount, request);

            discount.setUpdateBy(jwt.getUserId());
            discount.setUpdateAt(new Timestamp(System.currentTimeMillis()));
            discount = discountRepository.save(discount);

            DiscountResponse discountResponse = new DiscountResponse();
            FnCommon.copyNonNullProperties(discountResponse, discount);

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
    @Override
    public double calulateDiscount(Long voucherId, double originalPrice, String token) {
        JwtResponse jwt = jwtService.decodeToken(token);

        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.VOUCHER_NOT_FOUND)
        );

        if (voucher.getUsedAt() != null) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.VOUCHER_ALREADY_USED);
        }

        Discount discount = discountRepository.findById(voucher.getDiscount().getId()).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_NOT_FOUND)
        );

        Condition condition = discount.getCondition();
        boolean discountApplied = false;
        if (condition != null) {
            if (!discountApplied && condition.getProductDetailId() != null) {
                if (productDetailRepository.findById(condition.getProductDetailId()).orElse(null) != null) {
                    discountApplied = true;
                }
            }

            if (!discountApplied && condition.getProductId() != null) {
                if (productRepository.findById(condition.getProductId()).orElse(null) != null) {
                    discountApplied = true;
                }
            }

            if (!discountApplied && condition.getCategoryId() != null) {
                if (categoryRepository.findById(condition.getCategoryId()).orElse(null) != null) {
                    discountApplied = true;
                }
            }

            if (!discountApplied && condition.getBrandId() != null) {
                if (brandRepository.findById(condition.getBrandId()).orElse(null) != null) {
                    discountApplied = true;
                }
            }

            if (!discountApplied) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_CONDITION_NOT_MET);
            }
        }


        // Tính toán giá trị giảm giá dựa trên loại giảm giá (phần trăm hoặc cố định)
        double discountAmount;
        if (discount.getType() == TypeDiscount.PERCENTAGE) {
            // Tính phần trăm giảm giá
            discountAmount = originalPrice * (discount.getValue() / 100);
        } else {
            // Giảm giá cố định
            discountAmount = discount.getValue();
        }

        // Đảm bảo giá trị giảm giá không vượt quá giá trị tối đa (nếu có)
        if (discount.getMaxValue() != null && discountAmount > discount.getMaxValue()) {
            discountAmount = discount.getMaxValue();
        }

        // Tính toán giá cuối cùng sau khi áp dụng giảm giá
        double finalPrice = originalPrice - discountAmount;


        voucher.setUsedBy(jwt.getUserId());
        voucher.setUsedAt(new Timestamp(System.currentTimeMillis()));

        voucherRepository.save(voucher);

        return finalPrice;
    }
}
