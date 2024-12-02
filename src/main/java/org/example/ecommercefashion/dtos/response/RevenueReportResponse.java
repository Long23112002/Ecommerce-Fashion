package org.example.ecommercefashion.dtos.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RevenueReportResponse {
    private long total;
    private List<RevenueDataResponse> data;
}
