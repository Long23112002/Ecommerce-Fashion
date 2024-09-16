package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ColorRequest;
import org.example.ecommercefashion.dtos.request.SizeRequest;
import org.example.ecommercefashion.dtos.response.ApiResponse;
import org.example.ecommercefashion.dtos.response.ColorResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.SizeResponse;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.services.ColorService;
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
@RequestMapping("api/v1/color")
@RequiredArgsConstructor
@Api(tags = "Color", value = "Endpoints for color")
public class ColorController {
    private final ColorService colorService;

    @GetMapping
    public ResponsePage<Color, ColorResponse> getColorPage(@RequestParam(defaultValue = "", required = false) String name,
                                                           Pageable pageable) {
        return colorService.getColorPage(name, pageable);
    }

    @GetMapping("{id}")
    public ColorResponse getColorById(@PathVariable("id") Long id) {
        return colorService.getColorById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ColorResponse createColor(@RequestBody @Valid ColorRequest colorRequest, @RequestHeader ("Authorization") String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        return colorService.createColor(colorRequest, token);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ColorResponse updateColor(@PathVariable Long id,@RequestBody @Valid ColorRequest colorRequest, @RequestHeader ("Authorization") String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        return colorService.updateColor(colorRequest,id, token);
    }


    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Object> deleteColor(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String result = colorService.deleteColor(id,token);
        return ApiResponse.builder()
                .code(200)
                .message(result)
                .build();
    }
}
