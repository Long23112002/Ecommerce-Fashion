package org.example.ecommercefashion.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SoldProductResponse {
    private Long id;
    private String name;
    private Integer quantity;
}
