package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import com.longnh.utils.FnCommon;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ecommercefashion.dtos.request.TemplateRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.TemplateResponse;
import org.example.ecommercefashion.entities.Template;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.TemplateRepository;
import org.example.ecommercefashion.services.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceImpl implements TemplateService {
    private final TemplateRepository templateRepository;

    private TemplateResponse mapToResponse(Template template) {
        TemplateResponse response = new TemplateResponse();
        FnCommon.copyProperties(response, template);
        return response;
    }

    @Override
    public ResponsePage<Template, TemplateResponse> getAllTemplate(Pageable pageable) {
        Page<Template> pageTemplate = templateRepository.findAll(pageable);
        return new ResponsePage<>(pageTemplate, TemplateResponse.class);
    }

    @Override
    public TemplateResponse createTemplate(TemplateRequest request) {
        Template template = new Template();
        FnCommon.copyProperties(template, request);
        templateRepository.save(template);
        return mapToResponse(template);
    }

    @Override
    public TemplateResponse getTemplateById(Long id) {
        Template template = templateRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.TEMPLATE_NOT_FOUND));
        return mapToResponse(template);
    }

    @Override
    public TemplateResponse updateTemplate(Long id, TemplateRequest updateRequest) {
        Template template = templateRepository.findById(id).orElseThrow(
                () -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.TEMPLATE_NOT_FOUND));
        FnCommon.copyProperties(template, updateRequest);
        return mapToResponse(templateRepository.save(template));
    }

    @Override
    public MessageResponse deleteTemplate(Long id) {
        StringBuilder message = new StringBuilder();
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.TEMPLATE_NOT_FOUND));
        if (template != null) {
            if (template.getIsDeleted() == false) {
                template.setIsDeleted(true);
                message.append("Template was restored successfully");
            } else {
                template.setIsDeleted(false);
                message.append("Template was deleted successfully");
            }
            templateRepository.save(template);
        }
        return MessageResponse.builder()
                .message(String.valueOf(message))
                .build();
    }

    @Override
    public Page<TemplateResponse> getAllTemplate(Pageable pageable, Boolean isDeleted) {
        Page<Template> templatePage;

        if (isDeleted != null) {
            templatePage = templateRepository.findByIsDeleted(isDeleted, pageable);
        } else {
            templatePage = templateRepository.findAll(pageable);
        }

        List<TemplateResponse> listTemplate = templatePage
                .stream()
                .map(this::mapToResponse)
//                .sorted(Comparator.comparing(TemplateResponse::getUpdatedAt).reversed())
                .collect(Collectors.toList());

        return new PageImpl<>(listTemplate, pageable, templatePage.getTotalElements());

    }
}
