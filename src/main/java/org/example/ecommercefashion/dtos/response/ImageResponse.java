package org.example.ecommercefashion.dtos.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.enums.StatusImage;
import org.example.ecommercefashion.enums.TypeImage;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageResponse {
    private Long idImage;
    private String fileName;
    private TypeImage contentType;
    private Long size;
    private String imageUrl;
    private Timestamp createdAt;
    private StatusImage status;
}
