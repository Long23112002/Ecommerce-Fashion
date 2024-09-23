package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.request.MaterialRequest;
import org.example.ecommercefashion.dtos.response.ApiResponse;
import org.example.ecommercefashion.dtos.response.MaterialResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Material;
import org.example.ecommercefashion.services.MaterialService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("api/v1/material")
@RequiredArgsConstructor
@Api(tags = "Material", value = "Endpoints for material")
public class MaterialController {
  private final MaterialService materialService;

  @GetMapping
  public ResponsePage<Material, MaterialResponse> getMaterialPage(
      @RequestParam(defaultValue = "", required = false) String name, Pageable pageable) {
    return materialService.getMaterialPage(name, pageable);
  }

  @GetMapping("/{id}")
  public MaterialResponse getMaterialById(@PathVariable Long id) {
    return materialService.getMaterialById(id);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public MaterialResponse createMaterial(
      @RequestBody @Valid MaterialRequest materialRequest,
      @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return materialService.createMaterial(materialRequest, token);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public MaterialResponse updateMaterial(
      @PathVariable Long id,
      @RequestBody @Valid MaterialRequest materialRequest,
      @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return materialService.updateMaterial(materialRequest, id, token);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ApiResponse<Object> deleteMaterial(
      @PathVariable Long id, @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    String result = materialService.deleteMaterial(id, token);
    return ApiResponse.builder().code(200).message(result).build();
  }
}
