package org.example.ecommercefashion.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandResponse {

    private Long id;

    private String name;

    private Timestamp createAt;

    private Timestamp updateAt;

    private Long createBy;

    private Long updateBy;

    private Boolean deleted ;
}
