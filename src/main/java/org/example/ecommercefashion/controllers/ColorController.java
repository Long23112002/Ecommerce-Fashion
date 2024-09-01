package org.example.ecommercefashion.controllers;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.ApiResponse;
import org.example.ecommercefashion.dtos.response.ColorResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.services.ColorService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/color")
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;

    @GetMapping
    public ResponsePage<Color, ColorResponse> getColorPage(@RequestParam(defaultValue = "",required = false) String color,
                                                           Pageable pageable){
        return colorService.getColorPage(color, pageable);
    }

    @GetMapping("{id}")
    public ColorResponse getColorById(@PathVariable("id") Long id){
        return colorService.getColorById(id);
    }


    @DeleteMapping("{id}")
    public ApiResponse<Object> deleteColor(@PathVariable Long id) {
        String result = colorService.deleteColor(id);
        return ApiResponse.builder()
                .code(200)
                .message(result)
                .build();
    }
}
