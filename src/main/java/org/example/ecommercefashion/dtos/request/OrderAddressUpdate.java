package org.example.ecommercefashion.dtos.request;

import lombok.Data;

@Data
public class OrderAddressUpdate {
    private int provinceID;
    private String provinceName;
    private int districtID;
    private String districtName;
    private String wardCode;
    private String wardName;
}
