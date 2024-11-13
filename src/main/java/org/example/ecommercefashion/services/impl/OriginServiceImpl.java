package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.annotations.normalized;
import org.example.ecommercefashion.dtos.filter.OriginParam;
import org.example.ecommercefashion.dtos.request.OriginRequest;
import org.example.ecommercefashion.dtos.response.BrandResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.OriginResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.OriginRepository;
import org.example.ecommercefashion.repositories.ProductRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.NotificationService;
import org.example.ecommercefashion.services.OriginService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;

import static org.example.ecommercefashion.annotations.normalized.normalizeString;


@Service
@RequiredArgsConstructor
public class OriginServiceImpl implements OriginService{

    private final OriginRepository repository;

    private final UserRepository userRepository;

    private final org.example.ecommercefashion.security.JwtService JwtService;

    private final ProductRepository productRepository;

    private final NotificationService notificationService;

    @Override
    public ResponsePage<Origin, OriginResponse> filterOrigin(OriginParam param, Pageable pageable){
        Page<Origin> originPage = repository.FilterOrigin(param,pageable);
        Page<OriginResponse> originResponsePage = originPage.map(origin -> mapSizeToSizeResponse(origin));
        return new ResponsePage<>(originResponsePage);
    }
    @Override
    public OriginResponse add(OriginRequest request, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Origin origin = new Origin();
            String normalizedCategoryName;
            try {
                normalizedCategoryName = normalizeString(request.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to normalize string", e);
            }
            FnCommon.copyNonNullProperties(origin,request);
            if(repository.existsByName(normalizedCategoryName)){
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NAME_EXISTED);
            }
            origin.setCreateBy(jwt.getUserId());
            origin = repository.save(origin);
            notificationService.sendNotificationToUsersWithPermission(origin.getCreateBy(), NotificationCode.CREATE_ORIGIN,origin.getName());
            OriginResponse response = new OriginResponse();
            FnCommon.copyNonNullProperties(response,origin);
            return response;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NOT_FOUND);
        }
    }
    @Override
    public OriginResponse getByOriginId(Long id) {
        Origin origin = repository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ORIGIN_NOT_FOUND)
        );
        OriginResponse response = new OriginResponse();
        FnCommon.copyNonNullProperties(response, origin);
        if (origin.getCreateBy() != null) {
            response.setCreateBy(getInfoUser(origin.getCreateBy()));
        }
        if (origin.getUpdateBy() != null) {
            response.setUpdateBy(getInfoUser(origin.getUpdateBy()));
        }
        return response;
    }
    @Override
    public OriginResponse update(OriginRequest request, Long id, String token) {
        if (token != null) {
            JwtResponse jwt = JwtService.decodeToken(token);
            Origin origin = repository.findById(id).orElseThrow(
                    () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ORIGIN_NOT_FOUND)
            );
            String normalizedCategoryName;
            String originName = origin.getName();
            try {
                normalizedCategoryName = normalizeString(request.getName());
            } catch (IOException e) {
                throw new RuntimeException("Failed to normalize string", e);
            }
            FnCommon.copyNonNullProperties(origin,request);
            if(repository.existsByName(normalizedCategoryName)){
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_NAME_EXISTED);
            }
            origin.setUpdateBy(jwt.getUserId());
            origin.setUpdateAt(new Timestamp(System.currentTimeMillis()));
            origin = repository.save(origin);
            notificationService.sendNotificationToUsersWithPermission(origin.getUpdateBy(), NotificationCode.UPDATE_ORIGIN,originName,origin.getName());
            OriginResponse response = new OriginResponse();
            FnCommon.copyNonNullProperties(response,origin);
            return response;

        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.INVALID_REFRESH_TOKEN);
        }
    }
    @Override
    public MessageResponse deleted(Long id) {
        Origin origin = repository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.ORIGIN_NOT_FOUND)
        );

        if(productRepository.existsByOrigin(origin)){
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.ORIGIN_HAS_PRODUCT);
        }

        origin.setDeleted(true);
        repository.save(origin);

        return MessageResponse.builder().message("Origin deleted successfully").build();
    }
    private UserResponse getInfoUser(Long id) {
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
    private OriginResponse mapSizeToSizeResponse(Origin origin) {
        OriginResponse originResponse = new OriginResponse();
        FnCommon.copyNonNullProperties(originResponse, origin);
        originResponse.setCreateBy(getInfoUser(origin.getCreateBy()));
        originResponse.setUpdateBy(getInfoUser(origin.getUpdateBy()));
        return originResponse;
    }
}
