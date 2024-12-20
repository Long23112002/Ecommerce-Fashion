package org.example.ecommercefashion.entities.value;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private Integer provinceID;
    private String provinceName;
    private Integer districtID;
    private String districtName;
    private String wardCode;
    private String wardName;
    private String specificAddress;
}
