package org.example.ecommercefashion.services.impl;

import static org.example.ecommercefashion.annotations.normalized.normalizeString;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.Material;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.entities.value.UserValue;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.BrandRepository;
import org.example.ecommercefashion.repositories.CategoryRepository;
import org.example.ecommercefashion.repositories.MaterialRepository;
import org.example.ecommercefashion.repositories.OriginRepository;
import org.example.ecommercefashion.repositories.ProductRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final ProductRepository productRepository;
  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final MaterialRepository materialRepository;
  private final OriginRepository originRepository;

  private UserResponse getInfoUser(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
    UserResponse userResponse = new UserResponse();
    FnCommon.copyNonNullProperties(userResponse, user);
    return userResponse;
  }

  private UserValue getInfoUserValue(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND));
    UserValue userValue = new UserValue();
    FnCommon.copyNonNullProperties(userValue, user);
    return userValue;
  }

  private Product findById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(
            () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
  }

  @Override
  public ResponsePage<Product, Product> filterProduct(ProductParam param, Pageable pageable) {
    Page<Product> productPage = productRepository.filterProduct(param, pageable);
    Page<Product> responses =
            productPage.map(
                    product -> {
                      if (product.getCreateBy() != null) {
                        product.setCreateByUser(getInfoUserValue(product.getCreateBy()));
                      }
                      if (product.getUpdateBy() != null) {
                        product.setUpdateByUser(getInfoUserValue(product.getUpdateBy()));
                      }
                      return product;
                    });
    return new ResponsePage<>(responses);
  }

  @Override
  public Product createProduct(ProductRequest request, String token) {
    if (token != null) {
      JwtResponse jwtResponse = jwtService.decodeToken(token);

      if (productRepository.existsByNameIgnoreCase(request.getName().trim())) {
        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NAME_EXISTED);
      }

      Brand brand =
          brandRepository
              .findById(request.getIdBrand())
              .orElseThrow(
                  () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.BRAND_NOT_FOUND));
      Category category =
          categoryRepository
              .findById(request.getIdCategory())
              .orElseThrow(
                  () ->
                      new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_NOT_FOUND));
      Material material =
          materialRepository
              .findById(request.getIdMaterial())
              .orElseThrow(
                  () ->
                      new ExceptionHandle(
                          HttpStatus.BAD_REQUEST, AttributeErrorMessage.MATERIAL_NOT_FOUND));
      Origin origin =
          originRepository
              .findById(request.getIdOrigin())
              .orElseThrow(
                  () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NOT_FOUND));

      Product productCreate = new Product();
      FnCommon.copyNonNullProperties(productCreate, request);
      productCreate.setCode("PH"+ productRepository.getLastValue());
      productCreate.setCreateBy(jwtResponse.getUserId());
      productCreate.setBrand(brand);
      productCreate.setCategory(category);
      productCreate.setMaterial(material);
      productCreate.setOrigin(origin);

      productCreate.setCreateByUser(getInfoUserValue(jwtResponse.getUserId()));
      productRepository.save(productCreate);

      return productCreate;
    } else {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
    }
  }

  @Override
  public Product updateProduct(Long id, ProductRequest request, String token) {
    if (token != null) {
      JwtResponse jwtResponse = jwtService.decodeToken(token);
      Product product =
          productRepository
              .findById(id)
              .orElseThrow(
                  () ->
                      new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));

      String normalizedProductName;
      try {
        normalizedProductName = normalizeString(request.getName());
      } catch (IOException e) {
        throw new RuntimeException("Failed to normalize string", e);
      }
      if (productRepository.existsByNameIgnoreCase(normalizedProductName)) {
        throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NAME_EXISTED);
      }

      Brand brand =
          brandRepository
              .findById(request.getIdBrand())
              .orElseThrow(
                  () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.BRAND_NOT_FOUND));
      Category category =
          categoryRepository
              .findById(request.getIdCategory())
              .orElseThrow(
                  () ->
                      new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_NOT_FOUND));
      Material material =
          materialRepository
              .findById(request.getIdMaterial())
              .orElseThrow(
                  () ->
                      new ExceptionHandle(
                          HttpStatus.BAD_REQUEST, AttributeErrorMessage.MATERIAL_NOT_FOUND));
      Origin origin =
          originRepository
              .findById(request.getIdOrigin())
              .orElseThrow(
                  () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NOT_FOUND));

      FnCommon.copyNonNullProperties(product, request);
      product.setDescription(request.getDescription());
      product.setUpdateBy(jwtResponse.getUserId());
      product.setBrand(brand);
      product.setCategory(category);
      product.setMaterial(material);
      product.setOrigin(origin);

      product.setCreateByUser(getInfoUserValue(product.getCreateBy()));
      product.setUpdateByUser(getInfoUserValue(jwtResponse.getUserId()));
      productRepository.save(product);

      return product;
    } else {
      throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
    }
  }

  @Override
  public Product getProductById(Long id) {
    Product product = findById(id);
    if (product.getCreateBy() != null) {
      product.setCreateByUser(getInfoUserValue(product.getCreateBy()));
    }
    if (product.getUpdateBy() != null) {
      product.setUpdateByUser(getInfoUserValue(product.getUpdateBy()));
    }
    return product;
  }

  @Override
  public MessageResponse updateStatus(Long id) {
    Product product = findById(id);
    product.setDeleted(true);
    productRepository.save(product);
    return MessageResponse.builder().message("Product deleted successfully").build();
  }
}
