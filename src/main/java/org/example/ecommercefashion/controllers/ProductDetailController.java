package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.annotations.CheckPermission;
import org.example.ecommercefashion.dtos.filter.ProductDetailParam;
import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.dtos.request.ProductDetailRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.services.ProductDetailService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product-detail")
@RequiredArgsConstructor
@Api(tags = "Product Detail", value = "Endpoints for product detail")
public class ProductDetailController {
  private final ProductDetailService service;

  @PostMapping
  @CheckPermission({"add_product_detail"})
  public ResponseEntity<ProductDetail> createProductDetail(
      @RequestBody @Valid ProductDetailRequest request,
      @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return ResponseEntity.ok(service.createProductDetail(request, token));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductDetail> detail(@PathVariable Long id) {
    return ResponseEntity.ok(service.detail(id));
  }

  @PutMapping("/{id}")
  @CheckPermission({"update_product_detail"})
  public ResponseEntity<ProductDetail> updateProductDetail(
      @PathVariable Long id,
      @RequestBody @Valid ProductDetailRequest request,
      @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return ResponseEntity.ok(service.updateProductDetail(id, request, token));
  }

  @GetMapping
  public ResponsePage<ProductDetail, ProductDetail> getAll(
      ProductDetailParam param, Pageable pageable) {
    return service.getAllPage(pageable, param);
  }

  @DeleteMapping("/{id}")
  @CheckPermission({"delete_product_detail"})
  public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
    MessageResponse messageResponse = service.delete(id);
    return ResponseEntity.ok(messageResponse);
  }

  @GetMapping("/product/{id}")
  public ResponsePage<ProductDetail, ProductDetail> getDetailByIdProduct(
      @PathVariable Long id, PageableRequest pageable) {
    return service.getDetailByIdProduct(id, pageable.toPageable());
  }
}
