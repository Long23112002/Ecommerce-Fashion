package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.SizeRequest;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.SizeResponse;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Size;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.SizeRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.SizeService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;

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
    public ResponsePage<Size, SizeResponse> getSizePage(String name, Pageable pageable) {
        Page<Size> sizePage = sizeRepository.getSizePage(name, pageable);
        Page<SizeResponse> sizeResponsePage = sizePage.map(size -> mapSizeToSizeResponse(size));
        return new ResponsePage<>(sizeResponsePage);
    }

    @Override
    public SizeResponse getSizeById(Long id) {
        Size size = sizeRepository.findById(id).orElseThrow(() -> {
            throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.SIZE_NOT_FOUND);
        });
        return mapSizeToSizeResponse(size);
    }

    @Override
    public SizeResponse createSize(SizeRequest sizeRequest, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Size size = new Size();

            if (sizeRepository.existsByName(sizeRequest.getName().trim())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.SIZE_NAME_EXISTED);
            }

            Size sizeCreate = mapSizeRequestToSize(size, sizeRequest);
            sizeCreate.setCreatedBy(getInforUser(jwtResponse.getUserId()).getId());
            sizeRepository.save(sizeCreate);
            SizeResponse sizeResponse = mapSizeToSizeResponse(sizeCreate);
            sizeResponse.setCreatedBy(getInforUser(jwtResponse.getUserId()));
            return sizeResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public SizeResponse updateSize(SizeRequest sizeRequest, Long id, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Size size = sizeRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.SIZE_NOT_FOUND);
            });
            boolean isNameDuplicate = sizeRepository.existsByName(sizeRequest.getName().trim());
            if (isNameDuplicate && !size.getName().trim().equals(sizeRequest.getName().trim())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.SIZE_NAME_EXISTED);
            }
            Size sizeUpdate = mapSizeRequestToSize(size, sizeRequest);
            sizeUpdate.setUpdatedBy(getInforUser(jwtResponse.getUserId()).getId());
            sizeUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            sizeRepository.save(sizeUpdate);
            SizeResponse sizeResponse = mapSizeToSizeResponse(sizeUpdate);
            sizeResponse.setUpdatedBy(getInforUser(jwtResponse.getUserId()));
            sizeResponse.setCreatedBy(getInforUser(size.getCreatedBy()));
            return sizeResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public String deleteSize(Long id, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Size size = sizeRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.SIZE_NOT_FOUND);
            });
            size.setUpdatedBy(getInforUser(jwtResponse.getUserId()).getId());
            size.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            size.setDeleted(true);
            sizeRepository.save(size);
            return "Size deleted successfully";
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }

    }

    private Size mapSizeRequestToSize(Size size, SizeRequest sizeRequest) {
        FnCommon.copyNonNullProperties(size, sizeRequest);
        return size;
    }

    private SizeResponse mapSizeToSizeResponse(Size size) {
        SizeResponse sizeResponse = new SizeResponse();
        FnCommon.copyNonNullProperties(sizeResponse, size);
        sizeResponse.setCreatedBy(getInforUser(size.getCreatedBy()));
        sizeResponse.setUpdatedBy(getInforUser(size.getUpdatedBy()));
        return sizeResponse;
    }

}
