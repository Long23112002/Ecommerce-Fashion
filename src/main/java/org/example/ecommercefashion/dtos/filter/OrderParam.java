package org.example.ecommercefashion.dtos.filter;

import lombok.Data;
import org.example.ecommercefashion.enums.OrderStatus;

@Data
public class OrderParam {
  private String keyword;
  private Long userId;
  private OrderStatus status;
  private String phoneNumber;
  private Integer day;
  private Integer month;
  private Integer year;
}
