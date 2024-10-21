package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ProductResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Product;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ResponsePage<Product, ProductResponse> filterProduct( ProductParam param, Pageable pageable);
    ProductResponse createProduct(ProductRequest request, String token);
    ProductResponse updateProduct(Long id, ProductRequest request, String token);
    ProductResponse getProductById(Long id);
    MessageResponse updateStatus(Long id);

}
