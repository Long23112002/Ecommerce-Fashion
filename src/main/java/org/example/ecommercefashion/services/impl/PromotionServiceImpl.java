package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.PromotionParam;
import org.example.ecommercefashion.dtos.request.PromotionRequest;
import org.example.ecommercefashion.dtos.response.PromotionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Promotion;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.PromotionRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.PromotionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

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
        Page<Promotion> promotionPage = promotionRepository.filterPromotion(param,pageable);
        Page<PromotionResponse> promotionResponsePage = promotionPage.map(this::mapPromotionToPromotionResponse);
        return new ResponsePage<>(promotionResponsePage);
    }

    @Override
    public PromotionResponse getPromotionById(Long id) {
        return null;
    }

    @Override
    public PromotionResponse createPromotion(PromotionRequest promotionRequest, String token) {
        return null;
    }

    @Override
    public PromotionResponse updatePromotion(PromotionRequest promotionRequest, Long id, String token) {
        return null;
    }

    @Override
    public String deletePromotion(Long id, String token) {
        return "";
    }

    private Promotion mapPromotionRequestToPromotion(PromotionRequest promotionRequest,Promotion promotion){
        FnCommon.copyNonNullProperties(promotion,promotionRequest);
        return promotion;
    }

    private PromotionResponse mapPromotionToPromotionResponse(Promotion promotion) {
        PromotionResponse promotionResponse = new PromotionResponse();
        FnCommon.copyNonNullProperties(promotionResponse, promotion);
        promotionResponse.setCreatedBy(getInforUser(promotion.getCreatedBy()));
        promotionResponse.setUpdatedBy(getInforUser(promotion.getUpdatedBy()));
        return promotionResponse;
    }

}
