package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.BrandParam;
import org.example.ecommercefashion.dtos.request.BrandRequest;
import org.example.ecommercefashion.dtos.request.CategoryRequest;
import org.example.ecommercefashion.dtos.response.BrandResponse;
import org.example.ecommercefashion.dtos.response.CategoryResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.BrandRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    private final UserRepository userRepository;

    private final org.example.ecommercefashion.security.JwtService JwtService;

    @Override
    public ResponsePage<Brand, BrandResponse> filterCategory(BrandParam param, Pageable pageable) {
        Page<Brand> brandPage = brandRepository.FilterBrand(param, pageable);
        return new ResponsePage<>(brandPage, BrandResponse.class);
    }

    @Override
    public BrandResponse add(BrandRequest request, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Brand brand = new Brand();
            FnCommon.copyNonNullProperties(brand,request);
            brand.setCreateBy(jwt.getUserId());
            brand = brandRepository.save(brand);
            BrandResponse response = new BrandResponse();
            FnCommon.copyNonNullProperties(response,brand);
            return response;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.BRAND_NOT_FOUND);
        }
    }
    @Override
    public BrandResponse getByBrandId(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.BRAND_NOT_FOUND)
        );
        BrandResponse response = new BrandResponse();
        FnCommon.copyNonNullProperties(response, brand);
        if (brand.getCreateBy() != null) {
            response.setCreateBy(getInfoUser(brand.getCreateBy()));
        }
        if (brand.getUpdateBy() != null) {
            response.setUpdateBy(getInfoUser(brand.getUpdateBy()));
        }
        return response;
    }
    @Override
    public BrandResponse update(BrandRequest request, Long id, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Brand brand = brandRepository.findById(id).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.BRAND_NOT_FOUND)
            );
            FnCommon.copyNonNullProperties(brand,request);
            brand.setUpdateBy(jwt.getUserId());
            brand = brandRepository.save(brand);
            BrandResponse response = new BrandResponse();
            FnCommon.copyNonNullProperties(response,brand);
            return response;

        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN);
        }
    }
    @Override
    public MessageResponse deleted(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.BRAND_NOT_FOUND)
        );

        brand.setDeleted(true);
        brandRepository.save(brand);

        return MessageResponse.builder().message("Brand deleted successfully").build();
    }
    private UserResponse getInfoUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND)
        );
        UserResponse userResponse = new UserResponse();
        FnCommon.copyNonNullProperties(userResponse, user);
        return userResponse;

    }
}
