package org.example.ecommercefashion.dtos.request;

import lombok.Data;
import org.example.ecommercefashion.annotations.ValidPhoneNumber;

@Data
public class OrderUpdateRequest {
    private String fullName;
    @ValidPhoneNumber
    private String phoneNumber;
    private String specificAddress;
    private String note;
}
