package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.ProductDetailParam;
import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.ProductDetailRequest;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ProductDetailResponse;
import org.example.ecommercefashion.dtos.response.ProductResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.services.ProductDetailService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/product-detail")
@RequiredArgsConstructor
@Api(tags = "Product Detail", value = "Endpoints for product detail")
public class ProductDetailController {
    private final ProductDetailService service;
    @PostMapping
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<ProductDetailResponse> createProductDetail(
            @RequestBody @Valid ProductDetailRequest request,
            @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(service.createProductDetail(request, token));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<ProductDetailResponse> getById(@PathVariable Long id){
//        return ResponseEntity.ok(service.getProductDetailById(id));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetail> detail(@PathVariable Long id){
        return ResponseEntity.ok(service.detail(id));
    }
    @PutMapping("/{id}")
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<ProductDetailResponse> updateProductDetail(
            @PathVariable Long id,
            @RequestBody @Valid ProductDetailRequest request,
            @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(service.updateProductDetail(id, request, token));
    }

    @GetMapping
    public ResponsePage<ProductDetail, ProductDetailResponse> getAll(ProductDetailParam param, Pageable pageable) {
        return service.getAllPage(pageable, param);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        MessageResponse messageResponse = service.delete(id);
        return ResponseEntity.ok(messageResponse);
    }
}