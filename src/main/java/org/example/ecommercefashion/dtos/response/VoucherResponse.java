package org.example.ecommercefashion.dtos.response;

import java.sql.Timestamp;
import java.util.UUID;

public interface VoucherResponse {
    Long getId();
    UUID getCode();
    Timestamp getCreateAt();
    Timestamp getUpdateAt();
    Timestamp getUsedAt();
    String getCreateBy();
    String getUpdateBy();
    String getUsedBy();
    Boolean getDeleted();
    Long getIdDiscount();
}
