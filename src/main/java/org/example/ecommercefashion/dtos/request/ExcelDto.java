package org.example.ecommercefashion.dtos.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExcelDto {

  @NotEmpty private MultipartFile file;

  @NotEmpty private MultipartFile fileResult;

  @NotBlank private String objectName;

  private Integer count;

  private Integer success;

  private Integer error;

  @NotNull private Long process;

  @NotNull private Long userId;

  private String description;
}
