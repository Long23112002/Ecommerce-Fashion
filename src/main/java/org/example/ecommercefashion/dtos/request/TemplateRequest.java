package org.example.ecommercefashion.dtos.request;

import javax.validation.constraints.NotBlank;
import java.util.List;
public class TemplateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Subject is required")
    private String subject;
    @NotBlank(message = "Html is required")
    private String html;
    private List<String> variables;
}
