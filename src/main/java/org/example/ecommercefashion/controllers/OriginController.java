package org.example.ecommercefashion.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.filter.BrandParam;
import org.example.ecommercefashion.dtos.filter.OriginParam;
import org.example.ecommercefashion.dtos.request.BrandRequest;
import org.example.ecommercefashion.dtos.request.OriginRequest;
import org.example.ecommercefashion.dtos.response.BrandResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.OriginResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.services.BrandService;
import org.example.ecommercefashion.services.OriginService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/origin")
@RequiredArgsConstructor
public class OriginController {
  private final OriginService originService;

  @GetMapping
  public ResponsePage<Origin, OriginResponse> getAll(OriginParam param, Pageable pageable) {
    return originService.filterOrigin(param, pageable);
  }

  @PostMapping
  @PreAuthorize(("hasRole('ROLE_ADMIN')"))
  public ResponseEntity<OriginResponse> add(
      @Valid @RequestBody OriginRequest request, @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return ResponseEntity.ok(originService.add(request, token));
  }

  @PutMapping("/{id}")
  @PreAuthorize(("hasRole('ROLE_ADMIN')"))
  public ResponseEntity<OriginResponse> update(
      @PathVariable long id,
      @Valid @RequestBody OriginRequest request,
      @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return ResponseEntity.ok(originService.update(request, id, token));
  }

  @GetMapping("/{id}")
  @PreAuthorize(("hasRole('ROLE_ADMIN')"))
  public ResponseEntity<OriginResponse> getFindById(@PathVariable Long id) {
    OriginResponse response = originService.getByOriginId(id);
    if (response != null) {
      return ResponseEntity.ok(response);
    } else {
      return null;
    }
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<MessageResponse> getDeleted(@PathVariable Long id) {
    MessageResponse messageResponse = originService.deleted(id);
    return ResponseEntity.ok(messageResponse);
  }
}
