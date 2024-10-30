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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            validateDiscountCondition(request.getCondition());
            FnCommon.copyNonNullProperties(discount, request);
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
            validateDiscountCondition(discount.getCondition());
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

    // ở frontend sẽ call api getAllDiscount-thoa man dieu kiện
    // api sẽ cần truyền những sản phẩm đã chọn @BodyRequest
    // method sẽ lấy tất cả discount trong data
    // lặp từng discount
    // kiểu tra điểu kiện -> những sản phẩm đã chọn có thỏa mãn discount không
    // true -> cho vào list return
    // return list
    @Override
    public List<Discount> getVoucher(List<ProductDetail> detailList) {
        List<Discount> allDiscounts = discountRepository.findAll();
        List<Discount> validDiscounts = allDiscounts.stream()
                .filter(discount -> {
                    Condition condition = discount.getCondition();
                    return detailList.stream().anyMatch(detail -> {
                        boolean isProductDetail = condition.getProductDetailId().stream().anyMatch(id -> id.equals(detail.getId()));

                        boolean isProduct =  condition.getProductId().stream().anyMatch(id -> id.equals(detail.getProduct().getId()));

                        boolean isCategory =  detail.getProduct().getCategory() != null
                                && condition.getCategoryId().stream().anyMatch(id -> id.equals(detail.getProduct().getCategory().getId()));

                        boolean isBrand =  detail.getProduct().getBrand() != null
                                && condition.getBrandId().stream().anyMatch(id -> id.equals(detail.getProduct().getBrand().getId()));

                        return isProductDetail || isProduct || isCategory || isBrand;
                   });
                })
                .collect(Collectors.toList());

        return validDiscounts;
    }
    public void validateDiscountCondition(Condition condition){
        if (condition.getProductId() != null && !condition.getProductId().isEmpty()) {
            for (Long productId : condition.getProductId()) {
                if (!productRepository.existsById(productId)) {
                    throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Product ID " + productId + " không tồn tại");
                }
            }
        }
        if (condition.getProductDetailId() != null && !condition.getProductDetailId().isEmpty()) {
            for (Long productDetailId: condition.getProductDetailId()) {
                if (!productDetailRepository.existsById(productDetailId)) {
                    throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "ProductDetailId ID " + productDetailId + " không tồn tại");
                }
            }
        }
        if (condition.getCategoryId() != null && !condition.getCategoryId().isEmpty()) {
            for (Long categoryId: condition.getCategoryId()) {
                if (!categoryRepository.existsById(categoryId)) {
                    throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Category ID " + categoryId + " không tồn tại");
                }
            }
        }
        if (condition.getBrandId() != null && !condition.getBrandId().isEmpty()) {
            for (Long brandId: condition.getBrandId()) {
                if (!brandRepository.existsById(brandId)) {
                    throw new ExceptionHandle(HttpStatus.BAD_REQUEST, "Brand ID " + brandId + " không tồn tại");
                }
            }
        }
    }

}
