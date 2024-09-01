package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.SizeRequest;
import org.example.ecommercefashion.dtos.response.ApiResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.SizeResponse;
import org.example.ecommercefashion.entities.Size;
import org.example.ecommercefashion.services.SizeService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/size")
@RequiredArgsConstructor
public class SizeController {

    private final SizeService sizeService;

    @GetMapping
    public ResponsePage<Size, SizeResponse> getSizePage(@RequestParam(defaultValue = "", required = false) String name,
                                                        Pageable pageable) {
        return sizeService.getSizePage(name, pageable);
    }

    @GetMapping("{id}")
    public SizeResponse getSizeById(@PathVariable Long id){
        return sizeService.getSizeById(id);
    }

    @PostMapping
    public SizeResponse createSize(@RequestBody @Valid SizeRequest sizeRequest){
        return sizeService.createSize(sizeRequest);
    }

    @DeleteMapping("{id}")
    public ApiResponse<Object> deleteSize(@PathVariable Long id) {
        String result = sizeService.deleteSize(id);
        return ApiResponse.builder()
                .code(200)
                .message(result)
                .build();
    }

}
