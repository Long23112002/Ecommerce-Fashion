package org.example.ecommercefashion.controllers;

import javax.validation.Valid;
import org.example.ecommercefashion.dtos.request.CartRequest;
import org.example.ecommercefashion.entities.Cart;
import org.example.ecommercefashion.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/carts")
public class CartController {

  @Autowired private CartService cartService;

  @PostMapping
  public Cart create(@Valid @RequestBody CartRequest cartRequest) {
    return cartService.create(cartRequest);
  }

  @PutMapping()
  public Cart update(
      @Valid @RequestBody CartRequest cartRequest, @RequestHeader("Authorization") String token) {
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }
    return cartService.update(cartRequest, token);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    cartService.delete(id);
  }

  @GetMapping("/{userId}")
  public Cart getCartByUserId(@PathVariable Long userId) {
    return cartService.getCartByUserId(userId);
  }
}
