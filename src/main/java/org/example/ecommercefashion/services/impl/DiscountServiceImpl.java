package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.DiscountParam;
import org.example.ecommercefashion.dtos.request.DiscountRequest;
import org.example.ecommercefashion.dtos.response.CategoryResponse;
import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.Condition;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.DiscountRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.repositories.VoucherRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.DiscountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.example.ecommercefashion.annotations.normalized.normalizeString;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final DiscountRepository discountRepository;
    private final ProductDetailRepository productDetailRepository;

    @Override
    public ResponsePage<Discount, DiscountResponse> filterDiscount(DiscountParam params, Pageable pageable) {
        Page<Discount> discountPage = discountRepository.getFilterDiscountPage(params, pageable);
        Page<DiscountResponse> DiscountResponsePage = discountPage.map(discount -> mapSizeToSizeResponse(discount));
        return new ResponsePage<>(DiscountResponsePage);
    }

    @Override
    public ResponsePage<Discount, DiscountResponse> getAll(Pageable pageable) {
        Page<Discount> discountPage = discountRepository.findAll(pageable);
        Page<DiscountResponse> DiscountResponsePage = discountPage.map(discount -> mapSizeToSizeResponse(discount));
        return new ResponsePage<>(DiscountResponsePage);
    }

    @Override
    public DiscountResponse add(DiscountRequest request, String token) {

        if (token != null) {
            JwtResponse jwt = jwtService.decodeToken(token);

            Double totalValue = null;

            if (request.getCondition() != null && request.getCondition().getIdProductDetail() != null) {
                List<Long> productDetailIds = request.getCondition().getIdProductDetail();
                if (!productDetailIds.isEmpty()) {
                    totalValue = productDetailRepository.calculateTotalPriceByIds(productDetailIds);
                }
            }

//            // Nếu không có ProductDetail, sử dụng price từ Condition
            if (totalValue == null && request.getCondition() != null) {
                totalValue = request.getCondition().getPrice();
            }

            if (totalValue != null) {
                if (request.getType() == TypeDiscount.PERCENTAGE) {
                    if (request.getValue() > 50 || request.getMaxValue() > totalValue * 0.5) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_VALUE_EXCEEDS_LIMIT);
                    }
                    if (request.getValue() < 0) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_PERCENTAGE_WRONG_FORMAT);
                    }
                } else if (request.getType() == TypeDiscount.FIXED_AMOUNT) {
                    if (request.getValue() > totalValue * 0.5) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_VALUE_EXCEEDS_LIMIT);
                    }
                    if (request.getValue() < 1000) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_AMOUNT_WRONG_FORMAT);
                    }
                }
            }


            String normalizedCategoryName;
            try {
                normalizedCategoryName = normalizeString(request.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to normalize string", e);
            }
            if (discountRepository.existsByName(normalizedCategoryName)) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_NAME_EXISTED);
            }
            if (request.getCondition() != null && request.getCondition().getPrice() == 0.0) {
                request.getCondition().setPrice(null);
            }
            Discount discount = new Discount();

            FnCommon.copyNonNullProperties(discount, request);
            setDiscountStatus(discount);
            validateDiscountCondition(discount.getCondition());
            discount.setCreateBy(jwt.getUserId());
            String randomPart = getRandomString(6);
            discount.setCode("PPHH" + discountRepository.getLastValue() + randomPart);
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

            Double totalValue = null;

            if (request.getCondition() != null && request.getCondition().getIdProductDetail() != null) {
                List<Long> productDetailIds = request.getCondition().getIdProductDetail();
                if (!productDetailIds.isEmpty()) {
                    totalValue = productDetailRepository.calculateTotalPriceByIds(productDetailIds);
                }
            }

            if (totalValue == null && request.getCondition() != null) {
                totalValue = request.getCondition().getPrice();
            }

            if (totalValue != null) {
                if (request.getType() == TypeDiscount.PERCENTAGE) {
                    if (request.getValue() > 50 || request.getMaxValue() > totalValue * 0.5) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_VALUE_EXCEEDS_LIMIT);
                    }
                    if (request.getValue() < 0) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_PERCENTAGE_WRONG_FORMAT);
                    }
                } else if (request.getType() == TypeDiscount.FIXED_AMOUNT) {
                    if (request.getValue() > totalValue * 0.5) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_VALUE_EXCEEDS_LIMIT);
                    }
                    if (request.getValue() < 1000) {
                        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_AMOUNT_WRONG_FORMAT);
                    }
                }
            }

            String normalizedCategoryName;
            try {
                normalizedCategoryName = normalizeString(request.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to normalize string", e);
            }

            Discount discount = discountRepository.findById(id).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.DISCOUNT_NOT_FOUND)
            );
            FnCommon.copyNonNullProperties(discount, request);
            if (discountRepository.existsByNameAndIdNot(normalizedCategoryName, discount.getId())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_NAME_EXISTED);
            }
            if (discount.getType() == TypeDiscount.FIXED_AMOUNT) {
                discount.setMaxValue(null);
            }
            validateDiscountCondition(discount.getCondition());
            setDiscountStatus(discount);
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
    public List<Discount> getVoucher(List<ProductDetail> detailList, Order order) {
        List<Discount> allDiscounts = discountRepository.findAll();
        List<Discount> validDiscounts = allDiscounts.stream()
                .filter(discount -> {
                    Condition condition = discount.getCondition();
                    return detailList.stream().anyMatch(detail -> {

                        boolean isProductDetail = condition.getIdProductDetail() != null
                                && condition.getIdProductDetail().stream().anyMatch(id -> id.equals(detail.getId()));

                        boolean isPriceConditionMet = condition.getPrice() != null
                                && order.getTotalMoney() >= condition.getPrice();

                        return isProductDetail || isPriceConditionMet;
                    });
                })
                .collect(Collectors.toList());

        return validDiscounts;
    }

    public void validateDiscountCondition(Condition condition) {
        if (condition.getIdProductDetail() != null && !condition.getIdProductDetail().isEmpty()) {
            List<Long> invalidIds = condition.getIdProductDetail().stream()
                    .filter(id -> !productDetailRepository.existsById(id))
                    .collect(Collectors.toList());

            if (!invalidIds.isEmpty()) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST,
                        "Các ID ProductDetail sau không tồn tại: " + invalidIds);
            }
        }
    }

    public static void setDiscountStatus(Discount discount) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (discount.getEndDate().before(discount.getStartDate())) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.DISCOUNT_START_DATE_OR_END_DATE_WRONG);
        }

        if (now.before(discount.getStartDate())) {
            discount.setDiscountStatus(StatusDiscount.UPCOMING);
        } else if (now.after(discount.getEndDate())) {
            discount.setDiscountStatus(StatusDiscount.ENDED);
        } else {
            discount.setDiscountStatus(StatusDiscount.ACTIVE);
        }
    }

    private DiscountResponse mapSizeToSizeResponse(Discount discount) {
        DiscountResponse discountResponse = new DiscountResponse();
        FnCommon.copyNonNullProperties(discountResponse, discount);
        if (discount.getCreateBy() != null) {
            discountResponse.setCreateBy(getInfoUser(discount.getCreateBy()));
        }
        if (discount.getUpdateBy() != null) {
            discountResponse.setUpdateBy(getInfoUser(discount.getUpdateBy()));
        }
        return discountResponse;
    }

    private String getRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}
