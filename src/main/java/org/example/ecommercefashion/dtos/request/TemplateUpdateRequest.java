package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateUpdateRequest {
    private String name;
    private String subject;
    private String html;
    private List<String> variables;

}
