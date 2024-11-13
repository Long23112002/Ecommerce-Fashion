package org.example.ecommercefashion.dtos.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.value.UserInfo;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExcelDto {

  @NotEmpty private MultipartFile file;

  @NotEmpty private MultipartFile fileResult;

  @NotBlank private String objectName;

  private Integer count = 0;

  private Integer success = 0;

  private Integer error = 0;

  private String typeFile;

  private Long process;

  private UserInfo userInfo;

  private String description;
}
