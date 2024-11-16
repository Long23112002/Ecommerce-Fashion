package org.example.ecommercefashion.dtos.request;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OrderAddressUpdate {

    @NotNull
    private int provinceID;

    @NotBlank
    private String provinceName;

    @NotNull
    private Integer districtID;

    @NotBlank
    private String districtName;

    @NotBlank
    private String wardCode;

    @NotBlank
    private String wardName;
}
