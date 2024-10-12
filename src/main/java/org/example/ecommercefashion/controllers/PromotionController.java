package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.filter.PromotionParam;
import org.example.ecommercefashion.dtos.request.PromotionRequest;
import org.example.ecommercefashion.dtos.response.PromotionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Promotion;
import org.example.ecommercefashion.services.PromotionService;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("{id}")
    public PromotionResponse getPromotionById(@PathVariable Long id) {
        return promotionService.getPromotionById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PromotionResponse createPromotion(
            @RequestBody @Valid PromotionRequest promotionRequest, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return promotionService.createPromotion(promotionRequest, token);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PromotionResponse updatePromotion(
            @RequestBody @Valid PromotionRequest promotionRequest, @PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return promotionService.updatePromotion(promotionRequest, id, token);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deletePromotion(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return promotionService.deletePromotion(id, token);
    }
}
