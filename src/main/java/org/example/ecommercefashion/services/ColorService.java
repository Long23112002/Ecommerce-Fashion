package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.ColorRequest;
import org.example.ecommercefashion.dtos.response.ColorResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Color;
import org.springframework.data.domain.Pageable;

public interface ColorService {
    ResponsePage<Color, ColorResponse> getColorPage(String name, Pageable pageable);

    ColorResponse getColorById(Long id);

    ColorResponse createColor(ColorRequest colorRequest);

    String deleteColor(Long id);
}
