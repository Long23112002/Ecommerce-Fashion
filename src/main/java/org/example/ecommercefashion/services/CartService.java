package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.CartRequest;
import org.example.ecommercefashion.dtos.response.CartValueResponse;
import org.example.ecommercefashion.entities.Cart;
import org.example.ecommercefashion.entities.value.CartValue;
import org.example.ecommercefashion.entities.value.CartValueInfo;

import java.util.List;

public interface CartService {

  Cart create(CartRequest cartRequest);

  Cart update(CartRequest cartRequest, String token);

  Cart update(CartRequest cartRequest, Long userId);

  void delete(String token);

  Cart getCartByUserId(Long userId);

  CartValueResponse validCart(List<CartValue> request);

  List<CartValueInfo> getCartValueInfos(List<CartValue> values);
}
