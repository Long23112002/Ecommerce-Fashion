package org.example.ecommercefashion.entities;

import java.util.Date;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.Data;
import org.example.ecommercefashion.enums.ProcessStatus;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "import_process")
public class ImportProcess {

  @Transient public static final String SEQUENCE = "import_process_seq";

  @Id private Long id;

  @Indexed private Boolean isDelete = false;

  private String objectName;

  private ProcessStatus status;

  private String filePath;

  private String fileName;

  private String fileResult;

  private Integer count = 0;

  private Integer success = 0;

  private Integer error = 0;

  private Long userId;

  private User user;

  private String description;

  private Date createdAt;

  private Date updatedAt;
}
