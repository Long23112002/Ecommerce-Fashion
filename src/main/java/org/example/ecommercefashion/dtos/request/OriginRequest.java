package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OriginRequest {
    @NotBlank(message = "name is required")
    @Size(min = 2, max = 50, message = "tên phải từ 2 đến 50 ký tự")
    private String name;

}
