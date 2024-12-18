package org.example.ecommercefashion.dtos.response;

import lombok.Builder;
import lombok.Data;
import org.example.ecommercefashion.entities.value.CartValue;

import java.util.List;

@Data
public class CartValueResponse {
    private List<CartValue> cartValues;
    private boolean valid;
}
