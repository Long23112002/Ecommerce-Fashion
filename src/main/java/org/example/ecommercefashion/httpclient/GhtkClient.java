package org.example.ecommercefashion.httpclient;

import org.example.ecommercefashion.dtos.request.GhtkFeeRequest;
import org.example.ecommercefashion.dtos.response.GhtkFeeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "ghtk-shipping-order-fee", url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order")
public interface GhtkClient {

    @GetMapping("/fee")
    GhtkFeeResponse shippingOrderFee(
            @RequestHeader("token") String token,
            @RequestParam Map<String, String> queryParams
    );

}
