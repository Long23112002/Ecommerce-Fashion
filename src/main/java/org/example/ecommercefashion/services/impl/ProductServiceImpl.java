package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.dtos.request.ProductRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ProductResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.Material;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.User;
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

import java.io.IOException;

import static org.example.ecommercefashion.annotations.normalized.normalizeString;

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

    private ProductResponse mapProductToResponse(Product product) {
        Long idUser = product.getCreateBy();

        ProductResponse response = new ProductResponse();
        FnCommon.copyNonNullProperties(response, product);

        response.setBrandName(product.getBrand().getName());
        response.setCategoryName(product.getCategory().getName());
        response.setMaterialName(product.getMaterial().getName());
        response.setOriginName(product.getOrigin().getName());
        response.setCreatedAt(product.getCreateAt());
        response.setCreatedBy(getInfoUser(idUser));

        return response;
    }

    @Override
    public ResponsePage<Product, ProductResponse> filterProduct(ProductParam param, Pageable pageable) {
        Page<Product> productPage = productRepository.filterProduct(param, pageable);
        Page<ProductResponse> responses = productPage.map(product -> mapProductToResponse(product));
        return new ResponsePage<>(responses);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);

            if (productRepository.existsByNameIgnoreCase(request.getName().trim())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NAME_EXISTED);
            }

            Brand brand = brandRepository.findById(request.getIdBrand()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.BRAND_NOT_FOUND));
            Category category = categoryRepository.findById(request.getIdCategory()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_NOT_FOUND));
            Material material = materialRepository.findById(request.getIdMaterial()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.MATERIAL_NOT_FOUND));
            Origin origin = originRepository.findById(request.getIdOrigin()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NOT_FOUND));

            Product productCreate = new Product();
            FnCommon.copyNonNullProperties(productCreate, request);
            productCreate.setCreateBy(jwtResponse.getUserId());
            productCreate.setBrand(brand);
            productCreate.setCategory(category);
            productCreate.setMaterial(material);
            productCreate.setOrigin(origin);
            productRepository.save(productCreate);

            ProductResponse productResponse = mapProductToResponse(productCreate);
            return productResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));

            String normalizedCategoryName;
            try {
                normalizedCategoryName = normalizeString(request.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to normalize string", e);
            }
//            FnCommon.copyNonNullProperties(product, request);
            if (brandRepository.existsByName(normalizedCategoryName)) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NAME_EXISTED);
            }

            Brand brand = brandRepository.findById(request.getIdBrand()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.BRAND_NOT_FOUND));
            Category category = categoryRepository.findById(request.getIdCategory()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_NOT_FOUND));
            Material material = materialRepository.findById(request.getIdMaterial()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.MATERIAL_NOT_FOUND));
            Origin origin = originRepository.findById(request.getIdOrigin()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NOT_FOUND));

            FnCommon.copyNonNullProperties(product, request);
            product.setDescription(request.getDescription());
            product.setUpdateBy(jwtResponse.getUserId());
            product.setBrand(brand);
            product.setCategory(category);
            product.setMaterial(material);
            product.setOrigin(origin);
            productRepository.save(product);

            ProductResponse productResponse = mapProductToResponse(product);
            productResponse.setUpdatedAt(product.getUpdateAt());
            productResponse.setUpdatedBy(getInfoUser(product.getUpdateBy()));

            FnCommon.copyNonNullProperties(productResponse, product);
            return productResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
        ProductResponse response = mapProductToResponse(product);
        if (product.getUpdateAt() != null) {
            response.setUpdatedAt(product.getUpdateAt());
        }
        if (product.getUpdateBy() != null) {
            response.setUpdatedBy(getInfoUser(product.getUpdateBy()));
        }
        return response;
    }

    @Override
    public MessageResponse updateStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.PRODUCT_NOT_FOUND));
        StringBuilder string = new StringBuilder();
        if (product.getDeleted() == true) {
            product.setDeleted(false);
            string.append("Cập nhật  ");
        } else {
            product.setDeleted(true);
            string.append("Xóa ");
        }
        ;
        productRepository.save(product);
        return MessageResponse.builder()
                .message(string + "sản phẩm " + product.getName() + " thành công.")
                .build();

    }

}
