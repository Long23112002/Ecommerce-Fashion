package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @EntityGraph(attributePaths = "roles")
  User findByEmail(String email);

}
