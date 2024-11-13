package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GhtkOrderRequest {
    private double totalMoney;
    private int toDistrictId;
    private String toWardCode;
    private int quantity;
}
