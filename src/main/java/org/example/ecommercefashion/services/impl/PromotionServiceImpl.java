package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.PromotionParam;
import org.example.ecommercefashion.dtos.request.PromotionRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.PromotionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.Promotion;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.enums.promotion.StatusPromotionEnum;
import org.example.ecommercefashion.enums.promotion.TypePromotionEnum;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.PromotionRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.NotificationService;
import org.example.ecommercefashion.services.PromotionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final ProductDetailRepository productDetailRepository;

    private final NotificationService notificationService;

    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    private UserResponse getInforUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = userRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND)
        );

        UserResponse userResponse = new UserResponse();
        FnCommon.copyNonNullProperties(userResponse, user);
        return userResponse;
    }

    @Override
    public ResponsePage<Promotion, PromotionResponse> getPromotionPage(PromotionParam param, Pageable pageable) {
        if (param.getStartDate() == null) {
            param.setStartDate("1970-01-01");
        }

        if (param.getEndDate() == null) {
            param.setEndDate("9999-12-31");
        }
        Page<Promotion> promotionPage = promotionRepository.filterPromotion(param, pageable);
        Page<PromotionResponse> promotionResponsePage = promotionPage.map(this::mapPromotionToPromotionResponse);
        return new ResponsePage<>(promotionResponsePage);
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PROMOTION_NOT_FOUND)
        );
        return mapPromotionToPromotionResponse(promotion);
    }

    @Override
    public PromotionResponse createPromotion(PromotionRequest promotionRequest, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);

            if (promotionRequest.getEndDate().before(new Timestamp(System.currentTimeMillis()))) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_END_DATE_WRONG);
            }

            if (promotionRequest.getTypePromotionEnum() == TypePromotionEnum.PERCENTAGE_DISCOUNT) {
                if (promotionRequest.getValue() < 0 || promotionRequest.getValue() > 100) {
                    throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_PERCENTAGE_WRONG_FORMAT);
                }
            } else if (promotionRequest.getTypePromotionEnum() == TypePromotionEnum.AMOUNT_DISCOUNT) {
                if (promotionRequest.getValue() < 1000) {
                    throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_AMOUNT_WRONG_FORMAT);
                }
            }

//            List<Promotion> overlappingPromotions = promotionRepository.findOverlappingPromotions(
//                    promotionRequest.getStartDate(), promotionRequest.getEndDate());
//
//            if (!overlappingPromotions.isEmpty()) {
//                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_DATE_OVERLAP);
//            }

            Promotion promotion = new Promotion();
            Promotion promotionCreate = mapPromotionRequestToPromotion(promotionRequest, promotion);
            promotionCreate.setCreatedBy(getInforUser(jwtResponse.getUserId()).getId());
            setPromotionStatus(promotionCreate);
            promotionRepository.save(promotionCreate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
            String startDateFormatted = promotionCreate.getStartDate().toLocalDateTime().format(formatter);
            String endDateFormatted = promotionCreate.getEndDate().toLocalDateTime().format(formatter);
            notificationService.sendNotificationToUsersWithPermission(promotionCreate.getCreatedBy(), NotificationCode.CREATE_PROMOTION, startDateFormatted, endDateFormatted);
            PromotionResponse promotionResponse = mapPromotionToPromotionResponse(promotionCreate);
            promotionResponse.setCreatedBy(getInforUser(jwtResponse.getUserId()));
            return promotionResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public PromotionResponse updatePromotion(PromotionRequest promotionRequest, Long id, String token) {

        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);

            if (promotionRequest.getEndDate().before(new Timestamp(System.currentTimeMillis()))) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_END_DATE_WRONG);
            }

            if (promotionRequest.getTypePromotionEnum() == TypePromotionEnum.PERCENTAGE_DISCOUNT) {
                if (promotionRequest.getValue() < 0 || promotionRequest.getValue() > 100) {
                    throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_PERCENTAGE_WRONG_FORMAT);
                }
            } else if (promotionRequest.getTypePromotionEnum() == TypePromotionEnum.AMOUNT_DISCOUNT) {
                if (promotionRequest.getValue() < 1000) {
                    throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_AMOUNT_WRONG_FORMAT);
                }
            }

//            List<Promotion> overlappingPromotions = promotionRepository.findOverlappingPromotionsExceptCurrent(
//                    promotionRequest.getStartDate(), promotionRequest.getEndDate(), id);
//
//            if (!overlappingPromotions.isEmpty()) {
//                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_DATE_OVERLAP);
//            }

            Promotion promotion = promotionRepository.findById(id).orElseThrow(() ->
                    new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PROMOTION_NOT_FOUND)
            );
            Promotion promotionUpdate = mapPromotionRequestToPromotion(promotionRequest, promotion);
            promotionUpdate.setUpdatedBy(getInforUser(jwtResponse.getUserId()).getId());
            promotionUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            setPromotionStatus(promotionUpdate);
            promotionRepository.save(promotionUpdate);
            notificationService.sendNotificationToUsersWithPermission(promotionUpdate.getUpdatedBy(), NotificationCode.UPDATE_PROMOTION, promotionUpdate.getId());
            PromotionResponse promotionResponse = mapPromotionToPromotionResponse(promotionUpdate);
            promotionResponse.setCreatedBy(getInforUser(promotion.getCreatedBy()));
            promotionResponse.setUpdatedBy(getInforUser(jwtResponse.getUserId()));

            return promotionResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public String deletePromotion(Long id, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Promotion promotion = promotionRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.SIZE_NOT_FOUND);
            });
            promotion.setUpdatedBy(getInforUser(jwtResponse.getUserId()).getId());
            promotion.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            promotion.getProductDetailList().clear();
                productDetailRepository.findAll().forEach(productDetail -> {
                    if (productDetail.getOriginPrice() != null) {
                        productDetail.setPrice(productDetail.getOriginPrice());
                        productDetail.setOriginPrice(null);
                    }
                });
            promotion.setDeleted(true);
            promotionRepository.save(promotion);
            return "Promotion deleted successfully";
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    private Promotion mapPromotionRequestToPromotion(PromotionRequest promotionRequest, Promotion promotion) {
        FnCommon.copyNonNullProperties(promotion, promotionRequest);
        return promotion;
    }

    private PromotionResponse mapPromotionToPromotionResponse(Promotion promotion) {
        PromotionResponse promotionResponse = new PromotionResponse();
        FnCommon.copyNonNullProperties(promotionResponse, promotion);
        promotionResponse.setCreatedBy(getInforUser(promotion.getCreatedBy()));
        promotionResponse.setUpdatedBy(getInforUser(promotion.getUpdatedBy()));
        if (promotionResponse.getTypePromotionEnum() == TypePromotionEnum.AMOUNT_DISCOUNT) {
            String formattedValue = decimalFormat.format(promotion.getValue());
            promotionResponse.setValue(formattedValue);
        } else if (promotionResponse.getTypePromotionEnum() == TypePromotionEnum.PERCENTAGE_DISCOUNT) {
            promotionResponse.setValue(promotion.getValue().toString());
        }
        return promotionResponse;
    }

    public static void setPromotionStatus(Promotion promotion) {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (promotion.getEndDate().before(promotion.getStartDate())) {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PROMOTION_START_DATE_OR_END_DATE_WRONG);
        }

        if (now.before(promotion.getStartDate())) {
            promotion.setStatusPromotionEnum(StatusPromotionEnum.UPCOMING);
        } else if (now.after(promotion.getEndDate())) {
            promotion.setStatusPromotionEnum(StatusPromotionEnum.ENDED);
        } else {
            promotion.setStatusPromotionEnum(StatusPromotionEnum.ACTIVE);
        }
    }

    @Override
    public PromotionResponse addProductDetailsToPromotion(Long promotionId, List<Long> productDetailIds, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);

            Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() ->
                    new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PROMOTION_NOT_FOUND)
            );

            promotion.setUpdatedBy(jwtResponse.getUserId());
            promotion.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if (productDetailIds == null || productDetailIds.isEmpty()) {
                promotion.getProductDetailList().clear();

                promotion.setUpdatedBy(jwtResponse.getUserId());
                promotion.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                promotionRepository.save(promotion);
                notificationService.sendNotificationToUsersWithPermission(promotion.getUpdatedBy(), NotificationCode.DELETE_PRODUCT_DETAIL_FROM_PROMOTION, promotionId);
                return mapPromotionToPromotionResponse(promotion);
            }

            List<ProductDetail> productDetails = productDetailRepository.findAllById(productDetailIds);

            List<ProductDetail> productDetailList = productDetailRepository.findAll();

            List<Long> foundIds = productDetails.stream()
                    .map(ProductDetail::getId)
                    .toList();

            List<Long> notFoundIds = productDetailIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            if (!notFoundIds.isEmpty()) {
                throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_DETAIL_NOT_FOUND);
            }

            List<Promotion> overlappingPromotions = promotionRepository.findOverlappingPromotions(
                    promotion.getStartDate(), promotion.getEndDate());

            for (Promotion overlappingPromotion : overlappingPromotions) {
                if (!overlappingPromotion.getId().equals(promotionId)) {
                    for (ProductDetail productDetail : productDetails) {
                        if (overlappingPromotion.getProductDetailList().contains(productDetail)) {
                            if (overlappingPromotion.getUpdatedAt().before(promotion.getUpdatedAt())) {
                                overlappingPromotion.getProductDetailList().remove(productDetail);
                                promotionRepository.save(overlappingPromotion);
                            }
                        }
                    }
                }
            }

            if (promotion.getStatusPromotionEnum().equals(StatusPromotionEnum.ACTIVE)) {
                productDetailList.forEach(productDetail -> {
                    if (productDetail.getOriginPrice() != null) {
                        productDetail.setPrice(productDetail.getOriginPrice());
                        productDetail.setOriginPrice(null);
                    }
                });
            }

            promotion.getProductDetailList().clear();

            promotion.getProductDetailList().addAll(productDetails);

            promotion.setUpdatedBy(jwtResponse.getUserId());
            promotion.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            promotionRepository.save(promotion);
            notificationService.sendNotificationToUsersWithPermission(promotion.getUpdatedBy(), NotificationCode.ADD_PRODUCT_DETAIL_TO_PROMOTION, promotionId);
            return mapPromotionToPromotionResponse(promotion);
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public List<ProductDetail> getOverlappingProductDetails(Long promotionId) {
        Promotion currentPromotion = promotionRepository.findById(promotionId).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.PROMOTION_NOT_FOUND)
        );
        List<Promotion> overlappingPromotions = promotionRepository.findOverlappingPromotions(
                currentPromotion.getStartDate(), currentPromotion.getEndDate()
        );
        Set<ProductDetail> overlappingProductDetails = new HashSet<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        for (Promotion overlappingPromotion : overlappingPromotions) {
            if (!overlappingPromotion.getId().equals(currentPromotion.getId())) {
                String formattedStartDate = formatter.format(overlappingPromotion.getStartDate().toInstant());
                String formattedEndDate = formatter.format(overlappingPromotion.getEndDate().toInstant());
                overlappingPromotion.setFormattedStartDate(formattedStartDate);
                overlappingPromotion.setFormattedEndDate(formattedEndDate);
                for (ProductDetail productDetail : overlappingPromotion.getProductDetailList()) {
                    productDetail.setPromotion(overlappingPromotion);
                    overlappingProductDetails.add(productDetail);
                }
            }
        }
        return new ArrayList<>(overlappingProductDetails);
    }


}
