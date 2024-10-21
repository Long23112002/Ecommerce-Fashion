package org.example.ecommercefashion.dtos.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Discount;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherResponse {

    private Long id;

    private UUID code;

    private Timestamp createAt;

    private Timestamp updateAt;

    private Timestamp usedAt;

    private UserResponse createBy;

    private UserResponse updateBy;

    private UserResponse usedBy;

    private Boolean deleted;

    private Discount discount;
}
