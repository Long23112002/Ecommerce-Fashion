package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.entities.OrderLog;
import org.example.ecommercefashion.entities.User;
import org.example.ecommercefashion.entities.value.Address;
import org.example.ecommercefashion.enums.OrderStatus;
import org.example.ecommercefashion.enums.PaymentMethodEnum;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long discountId;
    private User user;
    private OrderStatus status;
    private PaymentMethodEnum paymentMethod;
    private String fullName;
    private String phoneNumber;
    private Address address;
    private Timestamp shipdate;
    private String note;
    private Double moneyShip;
    private Double discountAmount;
    private Double totalMoney;
    private Double revenueAmount;
    private Double payAmount;
    private User updatedBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean deleted = false;
    private Long staffId;
    private List<OrderDetail> orderDetails;
    private List<OrderLog> orderLogs;
    private String code;
}
