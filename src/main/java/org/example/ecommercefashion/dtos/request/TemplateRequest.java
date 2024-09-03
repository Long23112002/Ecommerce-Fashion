package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Subject is required")
    private String subject;
    @NotBlank(message = "Html is required")
    private String html;
    private List<String> variables;

}
