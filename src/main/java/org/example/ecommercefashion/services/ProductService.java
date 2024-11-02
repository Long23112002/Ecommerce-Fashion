package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ProductResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Product;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ResponsePage<Product, Product> filterProduct(ProductParam param, Pageable pageable);
    Product createProduct(ProductRequest request, String token);
    Product updateProduct(Long id, ProductRequest request, String token);
    Product getProductById(Long id);
    MessageResponse updateStatus(Long id);

}
