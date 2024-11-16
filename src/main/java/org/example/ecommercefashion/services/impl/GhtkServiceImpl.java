package org.example.ecommercefashion.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.GhtkOrderRequest;
import org.example.ecommercefashion.dtos.response.GhtkFeeResponse;
import org.example.ecommercefashion.httpclient.GhtkClient;
import org.example.ecommercefashion.services.GhtkService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GhtkServiceImpl implements GhtkService {

    private final String TOKEN = "9612de31-80ce-11ef-be7f-626f70b8f792";
    private final int FROM_DISTRICT = 3440;
    private final int SERVICE_TYPE_ID = 2;
    private final int LENGTH = 30;
    private final int WIDTH = 30;
    private final int HEIGHT_UNIT = 5;
    private final int WEIGHT_UNIT = 500;

    private final GhtkClient ghnClient;

    @Override
    public GhtkFeeResponse getShippingFee(GhtkOrderRequest orderRequest) {
        int quantity = orderRequest.getQuantity();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("service_type_id", String.valueOf(SERVICE_TYPE_ID));
        queryParams.put("insurance_value", String.valueOf(Long.valueOf((long) orderRequest.getTotalMoney())));
        queryParams.put("coupon", "");
        queryParams.put("from_district_id", String.valueOf(FROM_DISTRICT));
        queryParams.put("to_district_id", String.valueOf(orderRequest.getToDistrictId()));
        queryParams.put("to_ward_code", orderRequest.getToWardCode());
        queryParams.put("height", String.valueOf(quantity * HEIGHT_UNIT));
        queryParams.put("weight", String.valueOf(quantity * WEIGHT_UNIT));
        queryParams.put("width", String.valueOf(WIDTH));
        queryParams.put("length", String.valueOf(LENGTH));

        return ghnClient.shippingOrderFee(TOKEN, queryParams);
    }
}

