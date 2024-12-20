package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.CategoryParam;
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
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.CategoryRepository;
import org.example.ecommercefashion.repositories.ProductRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.CategoryService;
import org.example.ecommercefashion.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.ecommercefashion.annotations.normalized.normalizeString;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final JwtService JwtService;

    private final NotificationService notificationService;

    private final ProductRepository productRepository;

    @Override
    public ResponsePage<Category, CategoryResponse> filterCategory(CategoryParam param, Pageable pageable) {
        Page<Category> CategoryPage = categoryRepository.filterCategories(param, pageable);
        Page<CategoryResponse> categoryResponses = CategoryPage.map(category -> mapCategoryToCategoryResponse(category));
        return new ResponsePage<>(categoryResponses);
    }

    @Override
    public ResponsePage<Category, CategoryResponse> getAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        Page<CategoryResponse> categoryResponses = categories.map(category -> mapCategoryToCategoryResponse(category));
        return new ResponsePage<>(categoryResponses);
    }

    @Override
    public CategoryResponse add(CategoryRequest request, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Category category = new Category();
            String normalizedCategoryName;
            try {
                normalizedCategoryName = normalizeString(request.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to normalize string", e);
            }
            FnCommon.copyNonNullProperties(category, request);

            if (categoryRepository.existsByName(normalizedCategoryName)) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_NAME_EXISTED);
            }
            Category parent = null;
            if (request.getParentId() != null) {
                Category categoryid = categoryRepository.findById(request.getParentId()).orElseThrow(
                        () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CATEGORY_NOT_FOUND));
                parent = categoryid;
            }
            category.setParentCategory(parent);
            category.setLever(category.calculateLevel());
            category.setCreateBy(jwt.getUserId());
            category.setUpdateAt(new Timestamp(System.currentTimeMillis()));
            category = categoryRepository.save(category);
            notificationService.sendNotificationToUsersWithPermission(category.getCreateBy(),NotificationCode.CREATE_CATEGORY,category.getName());
            CategoryResponse response = new CategoryResponse();
            FnCommon.copyNonNullProperties(response, category);

            return response;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }


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
    public CategoryResponse getByCategoryId(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CATEGORY_NOT_FOUND)
        );

        return mapCategoryToCategoryResponse(category);
    }

    @Override
    public CategoryResponse update(CategoryRequest request, Long id, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Category category = categoryRepository.findById(id).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CATEGORY_NOT_FOUND)
            );
            String normalizedCategoryName;
            String categoryName = category.getName();
            try {
                normalizedCategoryName = normalizeString(request.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to normalize string", e);
            }
            if (categoryRepository.existsByNameAndIdNot(normalizedCategoryName,category.getId())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_NAME_EXISTED);
            }
            category.setUpdateBy(jwt.getUserId());
            category.setName(request.getName());
            Category parent = null;
            if (request.getParentId() != null) {
                Category parentCategory = categoryRepository.findById(request.getParentId())
                        .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.CATEGORY_NOT_FOUND));
                parent = parentCategory;
            }
            category.setParentCategory(parent);
            category = categoryRepository.save(category);
            notificationService.sendNotificationToUsersWithPermission(category.getUpdateBy(), NotificationCode.UPDATE_CATEGORY,categoryName, category.getName());
            CategoryResponse response = new CategoryResponse();
            FnCommon.copyNonNullProperties(response, category);

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
        if(productRepository.existsByCategory(category)){
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CATEGORY_HAS_PRODUCT);
        }
        markAsDeleted(category);

        categoryRepository.save(category);

        return MessageResponse.builder().message("Category and related records deleted successfully").build();
    }

    private void markAsDeleted(Category category) {
        category.setDeleted(true);

        for (Category subCategory : category.getSubCategories()) {
            markAsDeleted(subCategory);
        }
    }

    private CategoryResponse mapCategoryToCategoryResponse(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse();
        FnCommon.copyNonNullProperties(categoryResponse, category);

        categoryResponse.setCreateBy(getInfoUser(category.getCreateBy()));
        categoryResponse.setUpdateBy(getInfoUser(category.getUpdateBy()));
        List<CategoryResponse> subcategoryResponses = category.getSubCategories().stream()
                .map(this::mapCategoryToCategoryResponse)
                .collect(Collectors.toList());
        categoryResponse.setSubCategories(subcategoryResponses);
        return categoryResponse;
    }

}
