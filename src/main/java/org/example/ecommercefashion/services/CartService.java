package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.CartRequest;
import org.example.ecommercefashion.entities.Cart;

public interface CartService {

  Cart create(CartRequest cartRequest);

  Cart update(CartRequest cartRequest, String token);

  void delete(String token);

  Cart getCartByUserId(Long userId);
}
