package org.example.ecommercefashion.repositories;

import java.util.List;
import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  @EntityGraph(attributePaths = "roles")
  User findByEmail(String email);

  @Query(
      "SELECT u FROM User u WHERE "
          + "(:#{#param.email} IS NULL OR u.slugEmail LIKE %:#{#param.email}%) AND "
          + "(:#{#param.phone} IS NULL OR u.phoneNumber LIKE %:#{#param.phone}%) AND "
          + "(:#{#param.fullName} IS NULL OR u.slugFullName LIKE %:#{#param.fullName}%) AND "
          + "(:#{#param.gender} IS NULL OR u.gender = :#{#param.gender}) "
          + "GROUP BY u.id, u.slugEmail, u.phoneNumber, u.slugFullName, u.gender "
          + "ORDER BY u.id DESC")
  Page<User> filterUser(UserParam param, Pageable pageable);

  @Query(
      value =
          "select count(u.id) > 0 "
              + "from users.user u "
              + "join users.user_roles ur "
              + "on ur.id_user = u.id "
              + "join users.role r "
              + "on r.id = ur.id_role "
              + "join users.role_permission rp "
              + "on rp.id_role = r.id "
              + "join users.permission p "
              + "on p.id = rp.id_permission "
              + "where u.id = :id "
              + "and p.name like :permission",
      nativeQuery = true)
  boolean isUserHasPermission(@Param("id") Long id, @Param("permission") String permission);

  @Query(
      value =
          "select distinct u.* "
              + "from users.user u "
              + "join users.user_roles ur "
              + "on ur.id_user = u.id "
              + "join users.role r "
              + "on r.id = ur.id_role "
              + "join users.role_permission rp "
              + "on rp.id_role = r.id "
              + "join users.permission p "
              + "on p.id = rp.id_permission "
              + "where p.name like :permission "
              + "and u.deleted = false",
      nativeQuery = true)
  List<User> findAllUserByPermission(String permission);

  boolean existsByEmailAndDeleted(String email, boolean deleted);

  boolean existsByPhoneNumberAndDeleted(String phoneNumber, boolean deleted);
}
