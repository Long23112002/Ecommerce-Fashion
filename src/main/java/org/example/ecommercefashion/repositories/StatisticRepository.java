package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StatisticRepository {

    List<Object[]> getCurrentDayRevenue();

    List<Object[]> getYearRevenueData(int year);

    List<Object[]> getMonthRevenueData(int year, int month);

    List<Object[]> getSoldProducts(int year, int month);

    Page<Object[]> getInventoryProduct(PageableRequest pageable);

}
