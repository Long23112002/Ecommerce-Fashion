package org.example.ecommercefashion.services;

import java.io.IOException;
import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
  ResponsePage<Product, Product> filterProduct(ProductParam param, Pageable pageable);

  ResponsePage<Product, Product> similarProduct(Long id, Pageable pageable);

  ResponsePage<Product, Product> hotProducts(Pageable pageable);

  ResponsePage<Product, Product> productInPromotion(Pageable pageable);

  Product createProduct(ProductRequest request, String token);

  Product updateProduct(Long id, ProductRequest request, String token);

  Product getProductById(Long id);

  MessageResponse updateStatus(Long id);

  byte[] exSampleTemplate() throws IOException;

  void importData(MultipartFile file, String token) throws IOException;

  byte[] exportData(Pageable pageable, ProductParam param) throws IOException;
}
