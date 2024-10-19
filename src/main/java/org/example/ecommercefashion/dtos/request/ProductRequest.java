package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được trống")
    @Size(min = 2, max = 50, message = "Tên sản phẩm phải từ 2 đến 50 ký tự")
    private String name;
    private String description;
    @NotNull(message = "Vui lòng chọn brand")
    private Long idBrand;
    @NotNull(message = "Vui lòng chọn category")
    private Long idCategory;
    @NotNull(message = "Vui lòng chọn material")
    private Long idMaterial;
    @NotNull(message = "Vui lòng chọn origin")
    private Long idOrigin;
}
