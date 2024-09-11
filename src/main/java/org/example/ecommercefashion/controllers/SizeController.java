package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("api/v1/size")
@RequiredArgsConstructor
@Api(tags = "Size", value = "Endpoints for size")
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
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public SizeResponse createSize(@RequestBody @Valid SizeRequest sizeRequest, @RequestHeader ("Authorization") String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        return sizeService.createSize(sizeRequest, token);
    }

    @PutMapping("{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public SizeResponse updateSize(@PathVariable Long id,@RequestBody @Valid SizeRequest sizeRequest, @RequestHeader ("Authorization") String token){
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        return sizeService.updateSize(sizeRequest,id, token);
    }

    @DeleteMapping("{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Object> deleteSize(@PathVariable Long id, @RequestHeader ("Authorization") String token) {
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }
        String result = sizeService.deleteSize(id,token);
        return ApiResponse.builder()
                .code(200)
                .message(result)
                .build();
    }

}
