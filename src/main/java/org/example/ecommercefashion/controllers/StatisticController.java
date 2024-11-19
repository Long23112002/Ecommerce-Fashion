package org.example.ecommercefashion.controllers;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.CurrentDayReportResponse;
import org.example.ecommercefashion.dtos.response.RevenueReportResponse;
import org.example.ecommercefashion.dtos.response.SoldProductResponse;
import org.example.ecommercefashion.services.StatisticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistic")
@RequiredArgsConstructor
@Api(tags = "Statistics", value = "Endpoints for statistic management")
public class StatisticController {

    private final StatisticService getYearlyRevenueData;

    @GetMapping("/current-day")
    public CurrentDayReportResponse getCurrentDayRevenue() {
        return getYearlyRevenueData.getCurrentDayRevenue();
    }

    @GetMapping("/year")
    public RevenueReportResponse getYearRevenueData(@RequestParam(name = "year", required = false) Integer year) {
        return getYearlyRevenueData.getYearRevenueData(year);
    }

    @GetMapping("/month")
    public RevenueReportResponse getYearRevenueData(@RequestParam(name = "year", required = false) Integer year,
                                                    @RequestParam(name = "month", required = false) Integer month) {
        return getYearlyRevenueData.getMonthRevenueData(year, month);
    }

    @GetMapping("/sold-product")
    public List<SoldProductResponse> getSoldProducts(@RequestParam(name = "year", required = false) Integer year,
                                                     @RequestParam(name = "month", required = false) Integer month) {
        return getYearlyRevenueData.getSoldProducts(year,month);
    }

}
