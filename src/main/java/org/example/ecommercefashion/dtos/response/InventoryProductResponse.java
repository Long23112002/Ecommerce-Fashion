package org.example.ecommercefashion.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InventoryProductResponse {
    private Long id;
    private String name;
    private Integer quantity;
    private List<InventoryProductDetail> productDetails;

    @Data
    @Builder
    public static class InventoryProductDetail {
        private Long id;
        private String size;
        private String color;
        private Integer quantity;
    }
}
