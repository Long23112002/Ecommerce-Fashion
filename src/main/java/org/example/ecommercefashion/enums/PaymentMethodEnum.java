package org.example.ecommercefashion.enums;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum {
    VNPAY("vnpay"),
    VIET_QR("vietqr"),
    CASH("cash");

    PaymentMethodEnum(String val) {
        this.val = val;
    }

    private String val;

}
