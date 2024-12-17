package org.example.ecommercefashion.dtos.filter;

import lombok.Data;
import lombok.Setter;
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

    public String getKeywordDefault() {
        return getDefault(keyword, String.class);
    }

    public Long getUserIdDefault() {
        return getDefault(userId, Long.class);
    }

    public OrderStatus getStatusDefault() {
        return getDefault(status, OrderStatus.class);
    }

    public String getPhoneNumberDefault() {
        return getDefault(phoneNumber, String.class);
    }

    public Integer getDayDefault() {
        return getDefault(day, Integer.class);
    }

    public Integer getMonthDefault() {
        return getDefault(month, Integer.class);
    }

    public Integer getYearDefault() {
        return getDefault(year, Integer.class);
    }

    public <T> T getDefault(T obj, Class<T> clazz) {
        if (obj != null) {
            return obj;
        }
        if (clazz == String.class) {
            return (T) "";
        }
        if (clazz == Long.class) {
            return (T) Long.valueOf(0);
        }
        if (clazz == Integer.class) {
            return (T) Integer.valueOf(0);
        }
        if (clazz == OrderStatus.class) {
            return (T) OrderStatus.DRAFT;
        }
        return obj;
    }
}
