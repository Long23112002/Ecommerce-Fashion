package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.SizeRequest;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.SizeResponse;
import org.example.ecommercefashion.entities.Size;
import org.springframework.data.domain.Pageable;

public interface SizeService {
    ResponsePage<Size,SizeResponse> getSizePage(String name, Pageable pageable);

    SizeResponse getSizeById(Long id);

    SizeResponse createSize(SizeRequest sizeRequest,String token);

    SizeResponse updateSize(SizeRequest sizeRequest,Long id,String token);

    String deleteSize(Long id,String token);
}
