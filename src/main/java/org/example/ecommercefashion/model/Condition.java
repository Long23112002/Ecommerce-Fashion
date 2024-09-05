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
    private long ProductDetailId;
    private long ProductId;
    private long CategoryId;
    private long BrandId;
}
