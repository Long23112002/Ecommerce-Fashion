package org.example.ecommercefashion.services;


import org.example.ecommercefashion.dtos.filter.CategoryParam;
import org.example.ecommercefashion.dtos.request.CategoryRequest;
import org.example.ecommercefashion.dtos.response.CategoryResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Category;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    ResponsePage<Category, CategoryResponse> filterCategory(CategoryParam param, Pageable pageable);

    CategoryResponse add(CategoryRequest request, String token);

    CategoryResponse getByCategoryId(Long id);

    CategoryResponse update(CategoryRequest request, Long id, String token);

    MessageResponse deleted(Long id);
}
