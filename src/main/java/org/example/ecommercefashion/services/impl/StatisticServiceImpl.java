package org.example.ecommercefashion.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.response.CurrentDayReportResponse;
import org.example.ecommercefashion.dtos.response.RevenueDataResponse;
import org.example.ecommercefashion.dtos.response.RevenueReportResponse;
import org.example.ecommercefashion.dtos.response.SoldProductResponse;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.StatisticRepository;
import org.example.ecommercefashion.services.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Override
    public RevenueReportResponse getYearRevenueData(Integer year) {
        if (year == null) {
            year = Year.now().getValue();
        }
        List<Object[]> datas = statisticRepository.getYearRevenueData(year);
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
        List<Object[]> datas = statisticRepository.getMonthRevenueData(year, month);
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
        List<Object[]> datas = statisticRepository.getSoldProducts(year,month);
        List<SoldProductResponse> responses = datas.stream()
                .map(data -> SoldProductResponse.builder()
                        .id(Long.valueOf(data[0].toString()))
                        .name(data[1].toString())
                        .sold(Integer.valueOf(data[2].toString()))
                        .soldProductDetails(toSoldProductDetails(data[3]))
                        .build())
                .toList();
        return responses;
    }

    @Override
    public CurrentDayReportResponse getCurrentDayRevenue() {
        List<Object[]> datas = statisticRepository.getCurrentDayRevenue();

        RevenueDataResponse yesterday = getRevenueData(datas.get(0));
        RevenueDataResponse today = getRevenueData(datas.get(1));

        long revenueToday = today.getRevenue();
        long revenueYesterday = yesterday.getRevenue();
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
                .name(todayData[0].toString().trim())
                .revenue((Double.valueOf(todayData[1].toString())).longValue())
                .build();
    }

    private RevenueReportResponse getRevenueReport(List<Object[]> datas) {
        long total = 0l;
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

    private Double getIncrease(long revenueToday, long revenueYesterday) {
        if(revenueYesterday == 0) {
            return 0.0;
        }
        Double increase = ((double)(revenueToday - revenueYesterday)/ revenueYesterday)*100;
        Long scaledIncrease = Math.round(increase*100);
        increase = scaledIncrease.doubleValue()/100;
        return increase;
    }

    private List<SoldProductResponse.SoldProductDetail> toSoldProductDetails(Object data)  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<SoldProductResponse.SoldProductDetail> soldProductDetails = mapper.readValue(data.toString(), List.class);
            return soldProductDetails;
        }catch (JsonProcessingException e){
            throw new ExceptionHandle(HttpStatus.BAD_REQUEST, ErrorMessage.SOMETHING_WENT_WRONG);
        }
    }

}
