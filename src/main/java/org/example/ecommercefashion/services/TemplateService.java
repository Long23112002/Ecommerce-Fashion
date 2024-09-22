package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.TemplateRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.TemplateResponse;
import org.example.ecommercefashion.entities.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface TemplateService {
    ResponsePage<Template, TemplateResponse> getAllTemplate(Pageable pageable);
    TemplateResponse createTemplate(TemplateRequest request);
    TemplateResponse getTemplateById(Long id);
    TemplateResponse updateTemplate(Long id, TemplateRequest updateRequest);
    MessageResponse deleteTemplate(Long id);
    Page<TemplateResponse> getAllTemplate(Pageable pageable, Boolean isDeleted);
}
