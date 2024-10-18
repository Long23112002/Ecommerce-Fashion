package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.ProductDetailParam;
import org.example.ecommercefashion.dtos.request.ProductDetailRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ProductDetailResponse;
import org.example.ecommercefashion.dtos.response.ProductResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.Size;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ColorRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.ProductRepository;
import org.example.ecommercefashion.repositories.SizeRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.ProductDetailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDetailServiceImpl implements ProductDetailService {
    private final ProductDetailRepository productDetailRepository;
    private final ProductRepository productRepository;
    private final SizeRepository sizeRepository;
    private final ColorRepository colorRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private UserResponse getInfoUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = userRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND)
        );
        UserResponse userResponse = new UserResponse();
        FnCommon.copyNonNullProperties(userResponse, user);
        return userResponse;

    }

    @Override
    public ProductDetailResponse createProductDetail(ProductDetailRequest request, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);

            ProductDetail detail = new ProductDetail();
            FnCommon.copyNonNullProperties(detail, request);

            Product product = productRepository.findById(request.getIdProduct())
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
            Size size = sizeRepository.findById(request.getIdSize())
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.SIZE_NOT_FOUND));
            Color color = colorRepository.findById(request.getIdColor())
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.COLOR_NOT_FOUND));

            detail.setProduct(product);
            detail.setSize(size);
            detail.setColor(color);
            detail.setCreateBy(jwtResponse.getUserId());
            detail = productDetailRepository.save(detail);

            ProductDetailResponse response = mapDetailToResponse(detail);
            return response;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ERROR_WHEN_CREATE);
        }
    }

    @Override
    public ProductDetailResponse getProductDetailById(Long id) {
        ProductDetail detail = productDetailRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
        ProductDetailResponse response = mapDetailToResponse(detail);
        return response;
    }

    @Override
    public ProductDetailResponse updateProductDetail(Long id, ProductDetailRequest request, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);

            ProductDetail detail = productDetailRepository.findById(id)
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
            FnCommon.copyNonNullProperties(detail, request);

            Product product = productRepository.findById(request.getIdProduct())
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
            Size size = sizeRepository.findById(request.getIdSize())
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.SIZE_NOT_FOUND));
            Color color = colorRepository.findById(request.getIdColor())
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.COLOR_NOT_FOUND));

            detail.setProduct(product);
            detail.setSize(size);
            detail.setColor(color);
            detail.setImages(request.getImages());
            detail.setUpdateBy(jwtResponse.getUserId());
            detail = productDetailRepository.save(detail);

            ProductDetailResponse response = mapDetailToResponse(detail);
            return response;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ERROR_WHEN_CREATE);
        }
    }

    @Override
    public ResponsePage<ProductDetail, ProductDetailResponse> getAllPage(Pageable pageable, ProductDetailParam productDetailParam) {
        Page<ProductDetail> productDetailPage = productDetailRepository.filterProductDetail(productDetailParam, pageable);
        Page<ProductDetailResponse> responses = productDetailPage.map(detail -> mapDetailToResponse(detail));
        return new ResponsePage<>(responses);
    }

    @Override
    public MessageResponse delete(Long id) {
        return null;
    }

    ProductDetailResponse mapDetailToResponse(ProductDetail detail) {
        ProductDetailResponse response = new ProductDetailResponse();
        FnCommon.copyNonNullProperties(response, detail);
        response.setCreateBy(getInfoUser(detail.getCreateBy()));
        response.setUpdateBy(getInfoUser(detail.getUpdateBy()));
        response.setColorName(detail.getColor().getName());
        response.setSizeName(detail.getSize().getName());

        response.setProduct(mapProductToResponse(detail.getProduct()));
        return response;
    }

    ProductResponse mapProductToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .createdAt(product.getCreateAt())
                .updatedAt(product.getUpdateAt())
                .createdBy(getInfoUser(product.getCreateBy()))
                .brandName(product.getBrand().getName())
                .categoryName(product.getCategory().getName())
                .materialName(product.getMaterial().getName())
                .originName(product.getOrigin().getName())
                .build();
    }
}
