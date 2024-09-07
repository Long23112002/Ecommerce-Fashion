package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.UserParam;
import org.example.ecommercefashion.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    User findByEmail(String email);

    @Query(
            "SELECT u FROM User u WHERE "
                    + "(:#{#param.email} IS NULL OR u.email LIKE %:#{#param.email}%) AND "
                    + "(:#{#param.phone} IS NULL OR u.phoneNumber LIKE %:#{#param.phone}%) AND "
                    + "(:#{#param.fullName} IS NULL OR u.fullName LIKE %:#{#param.fullName}%) AND "
                    + "(:#{#param.gender} IS NULL OR u.gender = :#{#param.gender})")
    Page<User> filterUser(UserParam param, Pageable pageable);

    @Query(value = "select count(u.id) > 0 " +
            "from users.user u " +
            "join users.user_roles ur " +
            "on ur.id_user = u.id " +
            "join users.role r " +
            "on r.id = ur.id_role " +
            "join users.role_permission rp " +
            "on rp.id_role = r.id " +
            "join users.permission p " +
            "on p.id = rp.id_permission " +
            "where u.id = :id " +
            "and p.name like :permission",
            nativeQuery = true)
    boolean isUserHasPermission(@Param("id") Long id,
                                @Param("permission") String permission);

}
