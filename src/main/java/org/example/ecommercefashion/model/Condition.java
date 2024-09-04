package org.example.ecommercefashion.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    @NotNull(message = "min_purchase cannot be null")
    private Double min_purchase;
    @NotBlank(message = "category cannot be null")
    private String category;
}
