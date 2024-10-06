package org.example.ecommercefashion.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeRequest {

    @NotBlank(message = "Tên size không được trống")
    @Size(max = 50, message = "Tên size phải từ 2 đến 50 ký tự")
    private String name;

}
