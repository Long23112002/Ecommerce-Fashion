package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.CategoryRequest;
import org.example.ecommercefashion.dtos.response.CategoryResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.CategoryRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final JwtService JwtService;

    @Override
    public ResponsePage<Category, CategoryResponse> filterVoucher(String name, Integer lever, Pageable pageable) {
        Page<Category> CategoryResponsePage = categoryRepository.filterCategories(name, lever, pageable);
        return new ResponsePage<>(CategoryResponsePage, CategoryResponse.class);
    }

    @Override
    public CategoryResponse add(CategoryRequest request, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Category category = new Category();
            FnCommon.copyNonNullProperties(category, request);

            Category categoryid = categoryRepository.findById(request.getCategoryId()).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CATEGORY_NOT_FOUND));
            category.setParentCategory(categoryid);
            category = categoryRepository.save(category);
            CategoryResponse response = new CategoryResponse();
            FnCommon.copyNonNullProperties(response, category);
            response.setCreateBy(getInfoUser(jwt.getUserId()));
            return response;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }


    private UserResponse getInfoUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND)
        );
        UserResponse userResponse = new UserResponse();
        FnCommon.copyNonNullProperties(userResponse, user);
        return userResponse;

    }

    @Override
    public CategoryResponse getByCategoryId(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CATEGORY_NOT_FOUND)
        );
        CategoryResponse response = new CategoryResponse();
        FnCommon.copyNonNullProperties(response, category);
        if (category.getCreateBy() != null) {
            response.setCreateBy(getInfoUser(category.getCreateBy()));
        }
        if (category.getUpdateBy() != null) {
            response.setUpdateBy(getInfoUser(category.getUpdateBy()));
        }
        return response;
    }

    @Override
    public CategoryResponse update(CategoryRequest request, Long id, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Category category = categoryRepository.findById(id).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CATEGORY_NOT_FOUND)
            );

            FnCommon.copyNonNullProperties(category, request);
            category = categoryRepository.save(category);

            CategoryResponse response = new CategoryResponse();
            FnCommon.copyNonNullProperties(response, category);

            response.setUpdateBy(getInfoUser(jwt.getUserId()));

            return response;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    public MessageResponse deleted(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CATEGORY_NOT_FOUND)
        );
        category.setDeleted(true);
        categoryRepository.save(category);

        return MessageResponse.builder().message("Category deleted successfully").build();
    }
}
