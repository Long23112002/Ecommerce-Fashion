package org.example.ecommercefashion.dtos.response;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommercefashion.entities.File;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ProductDetailResponse {
    private Long id;
    private Double price;
    private Integer quantity;
    private List<File> images;
    private Timestamp createAt;
    private Timestamp updateAt;
    private UserResponse createBy;
    private UserResponse updateBy;
    private ProductResponse product;
    private String sizeName;
    private String colorName;
}
