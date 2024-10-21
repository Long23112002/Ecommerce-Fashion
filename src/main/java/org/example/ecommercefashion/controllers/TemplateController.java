package org.example.ecommercefashion.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.TemplateRequest;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.dtos.response.TemplateResponse;
import org.example.ecommercefashion.services.TemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/template")
@RequiredArgsConstructor
@Tag(name = "Template", description = "Endpoints for template management")
public class TemplateController {
    private final TemplateService service;
    @PostMapping
    public TemplateResponse createTemplate(@Valid @RequestBody TemplateRequest request) {
        return service.createTemplate(request);
    }
    @GetMapping("/{id}")
    private TemplateResponse getTemplateById(@PathVariable Long id){
        return service.getTemplateById(id);
    }
    @PutMapping("/{id}")
    private TemplateResponse updateTemplate(@PathVariable Long id, @Valid @RequestBody TemplateRequest request){
        return service.updateTemplate(id, request);
    }
    @DeleteMapping("/{id}")
    private MessageResponse deleteTemplateById(@PathVariable Long id){
        return service.deleteTemplate(id);
    }

//    @GetMapping
//    private ResponsePage<Template,TemplateResponse> getAll(
//            @RequestParam(defaultValue = "0", required = false) int page,
//            @RequestParam(defaultValue = "5", required = false) int size
//            ){
//        Pageable pageable = PageRequest.of(page, size);
//        return service.getAllTemplate(pageable);
//    }

    @GetMapping
    private Page<TemplateResponse> getAll(
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "5", required = false) int size,
            @RequestParam( required = false) Boolean isDeleted
    ){
        Pageable pageable = PageRequest.of(page, size);
        return service.getAllTemplate(pageable, isDeleted);
    }
}
