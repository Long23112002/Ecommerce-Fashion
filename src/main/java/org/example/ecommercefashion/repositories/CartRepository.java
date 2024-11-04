package org.example.ecommercefashion.repositories;

import java.util.Optional;
import org.example.ecommercefashion.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

  Optional<Cart> getCartByUserId(Long userId);

  boolean existsByUserId(Long userId);
}
