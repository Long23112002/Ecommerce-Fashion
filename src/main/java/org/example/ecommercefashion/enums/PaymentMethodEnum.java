package org.example.ecommercefashion.enums;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum {
    VNPAY("vnpay"),
    BANK_TRANSFER("bankTransfer"),
    CASH("cash");

    PaymentMethodEnum(String val) {
        this.val = val;
    }

    private String val;

}
