package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  @Query(
      "SELECT r FROM Role r "
          + "WHERE (:keyword IS NULL OR r.name LIKE %:keyword%) "
          + "GROUP BY r.name, r.id "
          + "ORDER BY r.id DESC")
  Page<Role> filterRoles(@Param("keyword") String keyword, Pageable pageable);

  boolean existsByName(String name);
}
