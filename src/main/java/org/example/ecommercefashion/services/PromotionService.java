package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.PromotionParam;
import org.example.ecommercefashion.dtos.request.PromotionRequest;
import org.example.ecommercefashion.dtos.response.PromotionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Promotion;
import org.springframework.data.domain.Pageable;

public interface PromotionService {
    ResponsePage<Promotion, PromotionResponse> getPromotionPage(PromotionParam param, Pageable pageable);

    PromotionResponse getPromotionById(Long id);

    PromotionResponse createPromotion(PromotionRequest promotionRequest, String token);

    PromotionResponse updatePromotion(PromotionRequest promotionRequest, Long id, String token);

    String deletePromotion(Long id, String token);
}
