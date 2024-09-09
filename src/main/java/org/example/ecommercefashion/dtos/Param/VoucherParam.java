package org.example.ecommercefashion.dtos.Param;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class VoucherParam {
    private String createAt;
    private Long createBy;
    private Long discountId;
}
