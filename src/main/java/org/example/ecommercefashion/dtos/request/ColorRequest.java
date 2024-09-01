package org.example.ecommercefashion.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorRequest {

    @NotBlank(message = "Color name is not null")
    private String name;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private Long createdBy;

    private Long updatedBy;

    private Boolean deleted = false;

}
