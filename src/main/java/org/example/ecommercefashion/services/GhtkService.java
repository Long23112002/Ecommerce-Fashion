package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.GhtkOrderRequest;
import org.example.ecommercefashion.dtos.response.GhtkFeeResponse;

public interface GhtkService {
    GhtkFeeResponse getShippingFee(GhtkOrderRequest orderRequest);
}
