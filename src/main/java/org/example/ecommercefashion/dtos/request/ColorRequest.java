package org.example.ecommercefashion.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorRequest {

    @NotBlank(message = "Color name is not null")
    @Size(min = 2, max = 50, message = "Color name is between 2 and 50 characters")
    private String name;

    private Long createdBy;

    private Long updatedBy;

    private Boolean deleted = false;

}
