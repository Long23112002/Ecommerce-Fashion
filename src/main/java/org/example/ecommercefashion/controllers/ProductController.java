package org.example.ecommercefashion.controllers;

import com.longnh.exceptions.ExceptionHandle;
import io.swagger.annotations.Api;
import java.io.IOException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.annotations.CheckPermission;
import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.services.ProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Api(tags = "Product", value = "Endpoints for product")
public class ProductController {
  private final ProductService productService;

  @GetMapping
  public ResponsePage<Product, Product> getAll(ProductParam param, PageableRequest pageable) {
    return productService.filterProduct(param, pageable.toPageable());
  }

  @GetMapping("/similar")
  public ResponsePage<Product, Product> similar(
      @RequestParam(name = "id") Long id, PageableRequest pageable) {
    return productService.similarProduct(id, pageable.toPageable());
  }

  @GetMapping("/hot")
  public ResponsePage<Product, Product> hot(PageableRequest pageable) {
    return productService.hotProducts(pageable.toPageable());
  }

  @GetMapping("/in_promotion")
  public ResponsePage<Product, Product> productInPromotion(PageableRequest pageable) {
    return productService.productInPromotion(pageable.toPageable());
  }

  @PostMapping
  @CheckPermission({"add_product"})
  public ResponseEntity<Product> createProduct(
      @Valid @RequestBody ProductRequest request, @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return ResponseEntity.ok(productService.createProduct(request, token));
  }

  @PutMapping("/{id}")
  @CheckPermission({"update_product"})
  public ResponseEntity<Product> updateProduct(
      @PathVariable Long id,
      @Valid @RequestBody ProductRequest request,
      @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return ResponseEntity.ok(productService.updateProduct(id, request, token));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Product> getById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProductById(id));
  }

  @DeleteMapping("/{id}")
  //  @CheckPermission({"delete_product"})
  public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
    MessageResponse messageResponse = productService.updateStatus(id);
    return ResponseEntity.ok(messageResponse);
  }

  @GetMapping("/export-sample-file")
  //  @CheckPermission({"export_sample_product"})
  public ResponseEntity<byte[]> exportSampleFile() throws IOException {
    try {
      byte[] content = productService.exSampleTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Disposition", "attachment; filename=example_import.xlsx");

      return new ResponseEntity<>(content, headers, HttpStatus.OK);
    } catch (IOException e) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.EXPORT_EXCEL_ERROR);
    }
  }

  @GetMapping("/export")
  public ResponseEntity<byte[]> exportData(ProductParam param, PageableRequest pageable)
      throws IOException {
    try {
      byte[] content = productService.exportData(pageable.toPageable(), param);

      HttpHeaders headers = new HttpHeaders();
      headers.add("Content-Disposition", "attachment; filename=product.xlsx");

      return new ResponseEntity<>(content, headers, HttpStatus.OK);
    } catch (IOException e) {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.EXPORT_EXCEL_ERROR);
    }
  }

  @PostMapping("/import")
  public void importt(MultipartFile file, @RequestHeader("Authorization") String token)
      throws IOException {
    try {
      productService.importData(file, token);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
