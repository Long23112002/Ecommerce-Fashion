package org.example.ecommercefashion.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeRequest {

    @NotBlank(message = "Size name is not null")
    private String name;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Long createdBy;

    private Long updatedBy;

    private Boolean deleted = false;

}
