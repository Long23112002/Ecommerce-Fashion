package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

    @PostMapping
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request,
                                                 @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return ResponseEntity.ok(productService.createProduct(request, token));
    }

    @PutMapping("/{id}")
    @PreAuthorize(("hasRole('ROLE_ADMIN')"))
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        MessageResponse messageResponse = productService.updateStatus(id);
        return ResponseEntity.ok(messageResponse);
    }


}
