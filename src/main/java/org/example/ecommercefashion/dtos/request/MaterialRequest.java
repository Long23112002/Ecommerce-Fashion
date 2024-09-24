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
    @NotBlank(message = "Tên chất liệu không được trống")
    @Size(min = 2, max = 50, message = "Tên chất liệu phải từ 2 đến 50 ký tự")
    private String name;

}
