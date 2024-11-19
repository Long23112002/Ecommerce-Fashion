package org.example.ecommercefashion.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RevenueReportResponse {
    private double total;
    private List<RevenueDataResponse> data;
}
