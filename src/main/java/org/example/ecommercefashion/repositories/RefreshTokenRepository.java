package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByToken(String token);

    List<RefreshToken> findAllValidTokenByUserId(Long userId);
}
