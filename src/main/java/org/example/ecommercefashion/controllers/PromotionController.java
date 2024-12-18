package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;

import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.annotations.CheckPermission;
import org.example.ecommercefashion.dtos.filter.PromotionParam;
import org.example.ecommercefashion.dtos.request.PromotionRequest;
import org.example.ecommercefashion.dtos.response.PromotionResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.Promotion;
import org.example.ecommercefashion.services.PromotionService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        return promotionService.getPromotionPage(param, pageable);
    }

    @GetMapping("/any_active")
    public boolean isAnyActive() {
        return promotionService.isAnyActive();
    }

    @GetMapping("{id}")
    public PromotionResponse getPromotionById(@PathVariable Long id) {
        return promotionService.getPromotionById(id);
    }

    @PostMapping
    @CheckPermission({"add_promotion"})
    public PromotionResponse createPromotion(
            @RequestBody @Valid PromotionRequest promotionRequest,
            @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return promotionService.createPromotion(promotionRequest, token);
    }

    @PutMapping("/{id}")
    @CheckPermission({"update_promotion"})
    public PromotionResponse updatePromotion(
            @RequestBody @Valid PromotionRequest promotionRequest,
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return promotionService.updatePromotion(promotionRequest, id, token);
    }

    @DeleteMapping("/{id}")
    @CheckPermission({"delete_promotion"})
    public String deletePromotion(
            @PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return promotionService.deletePromotion(id, token);
    }

    @PostMapping("/{promotionId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PromotionResponse> addProductDetailsToPromotion(
            @PathVariable Long promotionId,
            @RequestBody List<Long> productDetailIds,
            @RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        PromotionResponse updatedPromotion =
                promotionService.addProductDetailsToPromotion(promotionId, productDetailIds, token);

        return new ResponseEntity<>(updatedPromotion, HttpStatus.OK);
    }

    @GetMapping("/overlapping-products/{promotionId}")
    public ResponseEntity<List<ProductDetail>> getOverlappingProductDetails(
            @PathVariable Long promotionId
    ) {
        List<ProductDetail> productDetails = promotionService.getOverlappingProductDetails(promotionId);
        return ResponseEntity.ok(productDetails);
    }

}
