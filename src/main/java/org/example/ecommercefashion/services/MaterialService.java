package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.MaterialRequest;
import org.example.ecommercefashion.dtos.response.MaterialResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Material;
import org.springframework.data.domain.Pageable;

public interface MaterialService {

    ResponsePage<Material, MaterialResponse> getMaterialPage(String name, Pageable pageable);

    MaterialResponse getMaterialById(Long id);

    MaterialResponse createMaterial(MaterialRequest materialRequest, String token);

    MaterialResponse updateMaterial(MaterialRequest materialRequest, Long id, String token);

    String deleteMaterial(Long id, String token);

}
