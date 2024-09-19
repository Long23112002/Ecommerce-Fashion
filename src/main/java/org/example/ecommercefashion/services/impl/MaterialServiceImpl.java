package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.MaterialRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MaterialResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Material;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.MaterialRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.MaterialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor

public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private UserResponse getInforUser(Long id) {
        if (id == null) {
            return null;
        }
        User user = userRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND)
        );

        UserResponse userResponse = new UserResponse();
        FnCommon.copyNonNullProperties(userResponse, user);
        return userResponse;
    }

    @Override
    public ResponsePage<Material, MaterialResponse> getMaterialPage(String name, Pageable pageable) {
        Page<Material> materialPage = materialRepository.getMaterialPage(name, pageable);
        Page<MaterialResponse> materialResponse = materialPage.map(material -> mapMaterialToMaterialResponse(material));
        return new ResponsePage<>(materialResponse);
    }

    @Override
    public MaterialResponse getMaterialById(Long id) {
        Material material = materialRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.MATERIAL_NOT_FOUND)
        );
        return mapMaterialToMaterialResponse(material);
    }

    @Override
    public MaterialResponse createMaterial(MaterialRequest materialRequest, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Material material = new Material();
            Material mateialCreate = mapMaterialRequestToMaterial(materialRequest, material);
            if (materialRepository.existsByName(materialRequest.getName().trim())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.MATERIAL_NAME_EXISTED);
            }
            mateialCreate.setCreatedBy(getInforUser(jwtResponse.getUserId()).getId());
            materialRepository.save(mateialCreate);
            MaterialResponse materialResponse = mapMaterialToMaterialResponse(mateialCreate);
            materialResponse.setCreatedBy(getInforUser(jwtResponse.getUserId()));
            return materialResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public MaterialResponse updateMaterial(MaterialRequest materialRequest, Long id, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Material material = materialRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.MATERIAL_NOT_FOUND);
            });

            boolean isNameDuplicate = materialRepository.existsByName(materialRequest.getName().trim());
            if (isNameDuplicate && !material.getName().trim().equals(materialRequest.getName().trim())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.MATERIAL_NAME_EXISTED);
            }

            Material materialUpdate = mapMaterialRequestToMaterial(materialRequest, material);
            materialUpdate.setUpdatedBy(getInforUser(jwtResponse.getUserId()).getId());
            materialUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            materialRepository.save(materialUpdate);
            MaterialResponse materialResponse = mapMaterialToMaterialResponse(materialUpdate);
            materialResponse.setUpdatedBy(getInforUser(jwtResponse.getUserId()));
            materialResponse.setCreatedBy(getInforUser(material.getCreatedBy()));
            return materialResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public String deleteMaterial(Long id, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Material material = materialRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.MATERIAL_NOT_FOUND);
            });
            material.setUpdatedBy(getInforUser(jwtResponse.getUserId()).getId());
            material.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            material.setDeleted(true);
            materialRepository.save(material);
            return "Material deleted successfully";
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    private Material mapMaterialRequestToMaterial(MaterialRequest materialRequest, Material material) {
        FnCommon.copyNonNullProperties(material, materialRequest);
        return material;
    }

    private MaterialResponse mapMaterialToMaterialResponse(Material material) {
        MaterialResponse materialResponse = new MaterialResponse();
        FnCommon.copyNonNullProperties(materialResponse, material);
        materialResponse.setCreatedBy(getInforUser(material.getCreatedBy()));
        materialResponse.setUpdatedBy(getInforUser(material.getUpdatedBy()));
        return materialResponse;
    }
}
