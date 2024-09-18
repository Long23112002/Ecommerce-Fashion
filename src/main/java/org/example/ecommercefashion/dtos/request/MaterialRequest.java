package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequest {
    @NotBlank(message = "Material name is not null")
    @Size(min = 2, max = 50, message = "Material name is between 2 and 50 characters")
    private String name;

}
