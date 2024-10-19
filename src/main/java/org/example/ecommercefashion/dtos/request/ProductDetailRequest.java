package org.example.ecommercefashion.dtos.request;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommercefashion.entities.File;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ProductDetailRequest {
    @NotNull(message = "Giá không được trống")
    @Min(value = 1)
    private Double price;
    @NotNull(message = "Số lượng không được trống")
    @Min(value = 1)
    private Integer quantity;
    private List<File> images;
    @NotNull
    private Long idProduct;
    @NotNull(message = "Size không được trống")
    private Long idSize;
    @NotNull(message = "Màu sắc không được trống")
    private Long idColor;
}
