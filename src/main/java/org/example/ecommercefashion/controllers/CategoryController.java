package org.example.ecommercefashion.controllers;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.annotations.CheckPermission;
import org.example.ecommercefashion.dtos.filter.CategoryParam;
import org.example.ecommercefashion.dtos.request.CategoryRequest;
import org.example.ecommercefashion.dtos.response.CategoryResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.services.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public ResponsePage<Category, CategoryResponse> FiterAll(CategoryParam param, Pageable pageable) {
    return categoryService.filterCategory(param, pageable);
  }

  @GetMapping("/select")
  public ResponsePage<Category, CategoryResponse> getAll(Pageable pageable) {
    return categoryService.getAll(pageable);
  }

  @PostMapping
  @CheckPermission({"add_category"})
  public ResponseEntity<CategoryResponse> add(
      @Valid @RequestBody CategoryRequest request, @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return ResponseEntity.ok(categoryService.add(request, token));
  }

  @PutMapping("/{id}")
  @CheckPermission({"update_category"})
  public ResponseEntity<CategoryResponse> update(
      @PathVariable long id,
      @Valid @RequestBody CategoryRequest request,
      @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return ResponseEntity.ok(categoryService.update(request, id, token));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getFindById(@PathVariable Long id) {
    CategoryResponse response = categoryService.getByCategoryId(id);
    if (response != null) {
      return ResponseEntity.ok(response);
    } else {
      return null;
    }
  }

  @DeleteMapping("/{id}")
  @CheckPermission({"delete_category"})
  public ResponseEntity<MessageResponse> getDeleted(@PathVariable Long id) {
    MessageResponse messageResponse = categoryService.deleted(id);
    return ResponseEntity.ok(messageResponse);
  }
}
