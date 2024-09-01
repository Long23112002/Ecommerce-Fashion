package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.SizeRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private UserResponse getInforUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.USER_NOT_FOUND)
        );

        UserResponse userResponse = new UserResponse();
        FnCommon.copyNonNullProperties(userResponse,user);
        return userResponse;
    }

    @Override
    public ResponsePage<Size, SizeResponse> getSizePage(String name, Pageable pageable) {
        Page<Size> sizePage = sizeRepository.getSizePage(name, pageable);
        return new ResponsePage<>(sizePage, SizeResponse.class);
    }

    @Override
    public SizeResponse getSizeById(Long id) {
        Size size = sizeRepository.findById(id).orElseThrow(() -> {
            throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.SIZE_NOT_FOUND);
        });
        return mapSizeToSizeResponse(size);
    }

    @Override
    public SizeResponse createSize(SizeRequest sizeRequest,String token) {
        if(token!=null){
            JwtResponse response
        }
        Size size = new Size();
        Size sizeCreate = mapSizeRequestToSize(size, sizeRequest);
        return mapSizeToSizeResponse(sizeRepository.save(sizeCreate));
    }

    @Override
    public String deleteSize(Long id) {
        Size size = sizeRepository.findById(id).orElseThrow(() -> {
            throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.SIZE_NOT_FOUND);
        });
        size.setDeleted(true);
        sizeRepository.save(size);
        return "Size deleted successfully";
    }

    private Size mapSizeRequestToSize(Size size, SizeRequest sizeRequest) {
        FnCommon.copyNonNullProperties(size, sizeRequest);
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        size.setCreatedBy(username);
//        size.setUpdatedBy(username);
        return size;
    }

    private SizeResponse mapSizeToSizeResponse(Size size) {
        SizeResponse sizeResponse = new SizeResponse();
        FnCommon.copyNonNullProperties(sizeResponse, size);
        return sizeResponse;
    }

}
