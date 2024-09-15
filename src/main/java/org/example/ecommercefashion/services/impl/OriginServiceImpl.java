package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
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
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.OriginRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.services.OriginService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OriginServiceImpl implements OriginService{

    private final OriginRepository repository;

    private final UserRepository userRepository;

    private final org.example.ecommercefashion.security.JwtService JwtService;

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
            FnCommon.copyNonNullProperties(origin,request);
            origin.setCreateBy(jwt.getUserId());
            origin = repository.save(origin);
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
            FnCommon.copyNonNullProperties(origin,request);
            origin.setUpdateBy(jwt.getUserId());
            origin = repository.save(origin);
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
