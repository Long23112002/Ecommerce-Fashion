package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.filter.OriginParam;
import org.example.ecommercefashion.dtos.request.OriginRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.OriginResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Origin;
import org.springframework.data.domain.Pageable;

public interface OriginService {
    ResponsePage<Origin, OriginResponse> filterOrigin(OriginParam param, Pageable pageable);

    OriginResponse add(OriginRequest request, String token);

    OriginResponse getByOriginId(Long id);

    OriginResponse update(OriginRequest request, Long id, String token);

    MessageResponse deleted(Long id);
}
