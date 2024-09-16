package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Category;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private Long id;

    private String name;

    private Integer lever;

    private Timestamp createAt;

    private Timestamp updateAt;

    private UserResponse createBy;

    private UserResponse updateBy;

    private Boolean deleted ;

    private Category parentCategory;

    private List<CategoryResponse> subCategories;

}
