package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.filter.PromotionParam;
import org.example.ecommercefashion.dtos.response.PromotionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Promotion;
import org.example.ecommercefashion.services.PromotionService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/promotion")
@Api(tags = "Promotion", value = "Endpoints for promotion")
public class PromotionController {

    private final PromotionService promotionService;

    @GetMapping
    public ResponsePage<Promotion, PromotionResponse> getPromotionPage(
            PromotionParam param, Pageable pageable) {
        log.info("PromotionParam: {}", param);
        return promotionService.getPromotionPage(param, pageable);
    }
}
