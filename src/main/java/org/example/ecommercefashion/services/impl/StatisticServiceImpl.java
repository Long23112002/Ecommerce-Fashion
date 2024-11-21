package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.CurrentDayReportResponse;
import org.example.ecommercefashion.dtos.response.RevenueDataResponse;
import org.example.ecommercefashion.dtos.response.RevenueReportResponse;
import org.example.ecommercefashion.dtos.response.SoldProductResponse;
import org.example.ecommercefashion.repositories.OrderRepository;
import org.example.ecommercefashion.services.StatisticService;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final OrderRepository orderRepository;

    @Override
    public RevenueReportResponse getYearRevenueData(Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        List<Object[]> datas = orderRepository.getYearRevenueData(year);
        RevenueReportResponse response = getRevenueReport(datas);
        return response;
    }

    @Override
    public RevenueReportResponse getMonthRevenueData(Integer year, Integer month) {
        if (year == null) {
            year = Year.now().getValue();
        }
        if (month == null) {
            month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        }
        List<Object[]> datas = orderRepository.getMonthRevenueData(year, month);
        RevenueReportResponse response = getRevenueReport(datas);
        return response;
    }

    @Override
    public List<SoldProductResponse> getSoldProducts(Integer year, Integer month) {
        if (year == null) {
            year = Year.now().getValue();
        }
        if (month == null) {
            month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        }
        List<Object[]> datas = orderRepository.getSoldProducts(year,month);
        List<SoldProductResponse> responses = datas.stream()
                .map(data -> SoldProductResponse.builder()
                        .id(Long.valueOf(data[0].toString()))
                        .name(data[1].toString())
                        .quantity(Integer.valueOf(data[2].toString()))
                        .build())
                .toList();
        return responses;
    }

    @Override
    public CurrentDayReportResponse getCurrentDayRevenue() {
        List<Object[]> datas = orderRepository.getCurrentDayRevenue();

        RevenueDataResponse yesterday = getRevenueData(datas.get(0));
        RevenueDataResponse today = getRevenueData(datas.get(1));

        Double revenueToday = today.getRevenue();
        Double revenueYesterday = yesterday.getRevenue();
        Double increase = getIncrease(revenueToday, revenueYesterday);

        CurrentDayReportResponse response = CurrentDayReportResponse.builder()
                .increase(increase)
                .today(today)
                .yesterday(yesterday)
                .build();
        return response;
    }

    private RevenueDataResponse getRevenueData(Object[] todayData) {
        return RevenueDataResponse.builder()
                .name(todayData[0].toString())
                .revenue(Double.valueOf(todayData[1].toString()))
                .build();
    }

    private RevenueReportResponse getRevenueReport(List<Object[]> datas) {
        Double total = 0d;
        List<RevenueDataResponse> revenueDataResponses = new ArrayList<>();
        for (Object[] data : datas) {
            Double revenue = Double.valueOf(data[1].toString());
            total += revenue;
            RevenueDataResponse revenueDataResponse = getRevenueData(data);
            revenueDataResponses.add(revenueDataResponse);
        }
        return RevenueReportResponse.builder()
                .total(total)
                .data(revenueDataResponses)
                .build();
    }

    private Double getIncrease(Double revenueToday, Double revenueYesterday) {
        if(revenueYesterday == 0) {
            return 0.0;
        }
        Double increase = ((revenueToday - revenueYesterday)/ revenueYesterday)*100;
        Long scaledIncrease = Math.round(increase*100);
        increase = scaledIncrease.doubleValue()/100;
        return increase;
    }

}
