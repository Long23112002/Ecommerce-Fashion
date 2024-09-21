package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @EntityGraph(attributePaths = "roles")
  User findByEmail(String email);
  @EntityGraph(attributePaths = "roles")
  User findUserByEmailOrPhoneNumber(String email, String phoneNumber);
  @Query(
      "SELECT u FROM User u WHERE "
          + "(:#{#param.email} IS NULL OR u.email LIKE %:#{#param.email}%) AND "
          + "(:#{#param.phone} IS NULL OR u.phoneNumber LIKE %:#{#param.phone}%) AND "
          + "(:#{#param.fullName} IS NULL OR u.fullName LIKE %:#{#param.fullName}%) AND "
          + "(:#{#param.gender} IS NULL OR u.gender = :#{#param.gender})")
  Page<User> filterUser(UserParam param, Pageable pageable);
}
