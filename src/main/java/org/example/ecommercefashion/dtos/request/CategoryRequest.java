package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Name cannot be null")
    private String name;

    private Long parentId;
}
