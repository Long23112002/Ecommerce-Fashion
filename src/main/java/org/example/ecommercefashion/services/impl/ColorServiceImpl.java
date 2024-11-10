package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ColorRequest;
import org.example.ecommercefashion.dtos.response.ColorResponse;
import org.example.ecommercefashion.dtos.response.JwtResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.UserResponse;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.enums.notification.NotificationCode;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ColorRepository;
import org.example.ecommercefashion.repositories.ProductDetailRepository;
import org.example.ecommercefashion.repositories.UserRepository;
import org.example.ecommercefashion.security.JwtService;
import org.example.ecommercefashion.services.ColorService;
import org.example.ecommercefashion.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final NotificationService notificationService;

    private final ProductDetailRepository productDetailRepository;

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
    public ResponsePage<Color, ColorResponse> getColorPage(String name, Pageable pageable) {
        Page<Color> colorPage = colorRepository.getColorPage(name, pageable);
        Page<ColorResponse> colorResponsePage = colorPage.map(color -> mapColorToColorResponse(color));
        return new ResponsePage<>(colorResponsePage);
    }

    @Override
    public ColorResponse getColorById(Long id) {
        Color color = colorRepository.findById(id).orElseThrow(() -> {
            throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.COLOR_NOT_FOUND);
        });
        return mapColorToColorResponse(color);
    }

    @Override
    public ColorResponse createColor(ColorRequest colorRequest, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Color color = new Color();

            if (colorRepository.existsByNameIgnoreCase(colorRequest.getName().trim())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.COLOR_NAME_EXISTED);
            }

            if(colorRepository.existsByCode(colorRequest.getCode().trim())){
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CODE_COLOR_EXIST);
            }

            Color colorCreate = mapColorRequestToColor(color, colorRequest);
            colorCreate.setCreatedBy(getInforUser(jwtResponse.getUserId()).getId());
            colorRepository.save(colorCreate);
            ColorResponse colorResponse = mapColorToColorResponse(colorCreate);
            colorResponse.setCreatedBy(getInforUser(jwtResponse.getUserId()));
            return colorResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public ColorResponse updateColor(ColorRequest colorRequest, Long id, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Color color = colorRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.COLOR_NOT_FOUND);
            });
            boolean isNameDuplicate = colorRepository.existsByNameIgnoreCase(colorRequest.getName().trim());
            if (isNameDuplicate && !color.getName().trim().equalsIgnoreCase(colorRequest.getName().trim())) {
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, AttributeErrorMessage.COLOR_NAME_EXISTED);
            }
            boolean isCodeDuplicate = colorRepository.existsByCode(colorRequest.getCode().trim());
            if(isCodeDuplicate && !color.getCode().trim().equalsIgnoreCase(colorRequest.getCode().trim())){
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.CODE_COLOR_EXIST);
            }

            Color colorUpdate = mapColorRequestToColor(color, colorRequest);
            colorUpdate.setUpdatedBy(getInforUser(jwtResponse.getUserId()).getId());
            colorUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            colorRepository.save(colorUpdate);
            ColorResponse colorResponse = mapColorToColorResponse(colorUpdate);
            colorResponse.setUpdatedBy(getInforUser(jwtResponse.getUserId()));
            colorResponse.setCreatedBy(getInforUser(color.getCreatedBy()));

            return colorResponse;
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }
    }

    @Override
    public String deleteColor(Long id, String token) {
        if (token != null) {
            JwtResponse jwtResponse = jwtService.decodeToken(token);
            Color color = colorRepository.findById(id).orElseThrow(() -> {
                throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.COLOR_NOT_FOUND);
            });
            if(productDetailRepository.existsByColor(color)){
                throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.COLOR_HAS_PRODUCT_DETAIL);
            }
            color.setUpdatedBy(getInforUser(jwtResponse.getUserId()).getId());
            color.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            color.setDeleted(true);
            colorRepository.save(color);
            return "Color deleted successfully";
        } else {
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.USER_NOT_FOUND);
        }

    }

    private Color mapColorRequestToColor(Color color, ColorRequest colorRequest) {
        FnCommon.copyNonNullProperties(color, colorRequest);
        return color;
    }


    private ColorResponse mapColorToColorResponse(Color color) {
        ColorResponse colorResponse = new ColorResponse();
        FnCommon.copyNonNullProperties(colorResponse, color);
        colorResponse.setCreatedBy(getInforUser(color.getCreatedBy()));
        colorResponse.setUpdatedBy(getInforUser(color.getUpdatedBy()));
        return colorResponse;
    }
}
