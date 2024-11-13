package org.example.ecommercefashion.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GhtkFeeRequest {
    private int service_type_id;
    private double insurance_value;
    private String coupon;
    private int from_district_id;
    private int to_district_id;
    private String to_ward_code;
    private int height;
    private int weight;
    private int length;
    private int width;
}
