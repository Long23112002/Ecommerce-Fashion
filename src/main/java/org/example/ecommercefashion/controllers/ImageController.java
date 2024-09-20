package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ImageRequest;
import org.example.ecommercefashion.dtos.response.ImageResponse;
import org.example.ecommercefashion.services.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/images")
@RequiredArgsConstructor
@Api(tags = "Image", value = "Endpoints for image management")
public class ImageController {
  private final ImageService imageService;

  @PostMapping
  @PreAuthorize("hasRole('ROLE_STAFF')")
  public ResponseEntity<List<ImageResponse>> uploadImages(@ModelAttribute ImageRequest files) {
    List<ImageResponse> response = imageService.uploadImages(files);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{idImage}")
  public ResponseEntity<ImageResponse> getImageById(@PathVariable Long idImage) {
    ImageResponse response = imageService.getImageById(idImage);
    return ResponseEntity.ok(response);
  }
}
