package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.ProductDetailParam;
import org.example.ecommercefashion.dtos.request.ProductDetailRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ProductDetailResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.ProductDetail;
import org.springframework.data.domain.Pageable;

public interface ProductDetailService {
    ProductDetailResponse createProductDetail(ProductDetailRequest request, String token);
    ProductDetailResponse getProductDetailById(Long id);
    ProductDetailResponse updateProductDetail(Long id, ProductDetailRequest request, String token);
    ResponsePage<ProductDetail,ProductDetailResponse> getAllPage(Pageable pageable, ProductDetailParam productDetailParam);

    MessageResponse delete(Long id);
}
