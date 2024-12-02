package org.example.ecommercefashion.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SoldProductResponse {
    private Long id;
    private String name;
    private Integer sold;
    private List<SoldProductDetail> soldProductDetails;

    @Data
    @Builder
    public static class SoldProductDetail {
        private Long id;
        private String size;
        private String color;
        private Integer sold;
    }
}
