package org.example.ecommercefashion.controllers;

import javax.validation.Valid;
import org.example.ecommercefashion.dtos.request.CartRequest;
import org.example.ecommercefashion.dtos.response.CartValueResponse;
import org.example.ecommercefashion.entities.Cart;
import org.example.ecommercefashion.entities.value.CartValue;
import org.example.ecommercefashion.entities.value.CartValueInfo;
import org.example.ecommercefashion.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/carts")
public class CartController {

  @Autowired private CartService cartService;

  @PostMapping
  public Cart create(@Valid @RequestBody CartRequest cartRequest) {
    return cartService.create(cartRequest);
  }

  @PutMapping()
  public Cart update(@Valid @RequestBody CartRequest cartRequest,
                     @RequestHeader("Authorization") String token) {
    return cartService.update(cartRequest, token);
  }

  @DeleteMapping
  public void delete(@RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    cartService.delete(token);
  }

  @GetMapping("/{userId}")
  public Cart getCartByUserId(@PathVariable Long userId) {
    return cartService.getCartByUserId(userId);
  }

  @PostMapping("/info")
  public List<CartValueInfo> getCartValueInfos(@RequestBody List<CartValue> request) {
    return cartService.getCartValueInfos(request);
  }

  @PostMapping("/valid")
  public CartValueResponse valid(@RequestBody List<CartValue> request) {
    return cartService.validCart(request);
  }
}
