package org.example.ecommercefashion.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrentDayReportResponse {
    private double increase;
    private RevenueDataResponse today;
    private RevenueDataResponse yesterday;
}
