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

    @NotBlank(message = "Size name is not null")
    @Size(max = 50, message = "Size name is between 1 and 50 characters")
    private String name;

}
