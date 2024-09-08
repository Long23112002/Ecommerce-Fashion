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

    @NotNull(message = "Lever cannot be null")
    @Positive(message = "Lever must be a positive integer")
    private Integer lever;

    @PastOrPresent(message = "CreateAt must be in the past or present")
    private Timestamp createAt;

    @PastOrPresent(message = "UpdateAt must be in the past or present")
    private Timestamp updateAt;

    @NotNull(message = "CreateBy cannot be null")
    @Positive(message = "CreateBy must be a positive number")
    private Long createBy;

    @Positive(message = "CreateBy must be a positive number")
    private Long updateBy;

    @Positive(message = "CategoryId must be a positive number")
    private Long categoryId;
}
