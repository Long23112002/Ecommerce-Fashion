package org.example.ecommercefashion.dtos.request;

import lombok.Data;
import org.example.ecommercefashion.annotations.ValidPhoneNumber;
import org.example.ecommercefashion.enums.PaymentMethodEnum;

import javax.validation.constraints.NotBlank;

@Data
public class OrderUpdateRequest {

    @NotBlank(message = "Tên không được trống")
    private String fullName;

    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ cụ thể không được trống")
    private String specificAddress;

    private String note;

    private PaymentMethodEnum paymentMethod;
}
