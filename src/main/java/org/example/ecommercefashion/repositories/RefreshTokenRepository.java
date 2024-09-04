package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  @Query("SELECT rt FROM RefreshToken rt WHERE rt.token = :token")
  List<RefreshToken> findTokensByToken(@Param("token") String token);

  List<RefreshToken> findAllValidTokenByUserId(Long userId);

  RefreshToken findByToken(String token);
}
