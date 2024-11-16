package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GhtkFeeResponse {
    private int code;
    private String message;
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private int total;
        private double service_fee;
        private int insurance_fee;
        private int pick_station_fee;
        private int coupon_value;
        private int r2s_fee;
        private int return_again;
        private int document_return;
        private int double_check;
        private int cod_fee;
        private int pick_remote_areas_fee;
        private int deliver_remote_areas_fee;
        private int cod_failed_fee;
    }
}
