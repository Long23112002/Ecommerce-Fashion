package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.dtos.response.CurrentDayReportResponse;
import org.example.ecommercefashion.dtos.response.InventoryProductResponse;
import org.example.ecommercefashion.dtos.response.ResponsePage;
import org.example.ecommercefashion.dtos.response.RevenueReportResponse;
import org.example.ecommercefashion.dtos.response.SoldProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StatisticService {
    CurrentDayReportResponse getCurrentDayRevenue();
    RevenueReportResponse getYearRevenueData(Integer year);
    RevenueReportResponse getMonthRevenueData(Integer year, Integer month);
    List<SoldProductResponse> getSoldProducts(Integer year, Integer month);
    ResponsePage<Object[],InventoryProductResponse> getInventoryProduct(PageableRequest pageable);
}
