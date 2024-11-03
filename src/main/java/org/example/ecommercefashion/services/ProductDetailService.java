package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.ProductDetailParam;
import org.example.ecommercefashion.dtos.request.ProductDetailRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ProductDetailResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.ProductDetail;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductDetailService {
    ProductDetail createProductDetail(ProductDetailRequest request, String token);
    ProductDetail updateProductDetail(Long id, ProductDetailRequest request, String token);
    ResponsePage<ProductDetail,ProductDetail> getAllPage(Pageable pageable, ProductDetailParam productDetailParam);
    MessageResponse delete(Long id);
    ProductDetail detail(Long id);
    ResponsePage<ProductDetail, ProductDetail> getDetailByIdProduct(Long idProduct, Pageable pageable);
}
