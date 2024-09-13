package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.BrandParam;
import org.example.ecommercefashion.dtos.request.BrandRequest;
import org.example.ecommercefashion.dtos.response.BrandResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Brand;
import org.springframework.data.domain.Pageable;

public interface BrandService {
    ResponsePage<Brand, BrandResponse> filterCategory(BrandParam param, Pageable pageable);

    BrandResponse add(BrandRequest request, String token);

    BrandResponse getByBrandId(Long id);

    BrandResponse update(BrandRequest request, Long id, String token);

    MessageResponse deleted(Long id);
}
