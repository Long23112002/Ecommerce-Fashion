package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ColorRequest;
import org.example.ecommercefashion.dtos.response.ColorResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.exceptions.AttributeErrorMessage;
import org.example.ecommercefashion.repositories.ColorRepository;
import org.example.ecommercefashion.services.ColorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    @Override
    public ResponsePage<Color, ColorResponse> getColorPage(String name, Pageable pageable) {
        Page<Color> colorPage = colorRepository.getColorPage(name, pageable);
        return new ResponsePage<>(colorPage, ColorResponse.class);
    }

    @Override
    public ColorResponse getColorById(Long id) {
        Color color = colorRepository.findById(id).orElseThrow(()->{
            throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.COLOR_NOT_FOUND);
        });
        return mapColorToColorResponse(color);
    }

    @Override
    public ColorResponse createColor(ColorRequest colorRequest) {
        return null;
    }

    @Override
    public String deleteColor(Long id) {
        Color color = colorRepository.findById(id).orElseThrow(()->{
            throw new ExceptionHandle(HttpStatus.NOT_FOUND, AttributeErrorMessage.COLOR_NOT_FOUND);
        });
        color.setDeleted(true);
        colorRepository.save(color);
        return "Color deleted successfully";
    }


    private ColorResponse mapColorToColorResponse(Color color) {
        ColorResponse colorResponse = new ColorResponse();
        FnCommon.copyNonNullProperties(colorResponse, color);
        return colorResponse;
    }
}
