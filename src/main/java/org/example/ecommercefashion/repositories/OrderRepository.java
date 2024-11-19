package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

  @Query(
      "SELECT p FROM Order p WHERE p.status <> 'DRAFT' And"
          + "(:#{#param.userId} IS NULL OR p.user.id = :#{#param.userId}) AND "
          + "(:#{#param.status} IS NULL OR p.status = :#{#param.status}) AND "
          + "(:#{#param.keyword} IS NULL OR p.phoneNumber = cast( :#{#param.phoneNumber} as string ) )"
          + "AND (:#{#param.keyword} IS NULL OR LOWER(p.user.fullName) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%'))")
  Page<Order> filter(OrderParam param, Pageable pageable);

  @Query("SELECT COUNT(*) " +
          "FROM Order o " +
          "WHERE o.status = 'PENDING_AT_STORE' and o.deleted = false and o.staffId = :staffId ")
  Long countOrderPendingStore(Long staffId);

  @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC")
  List<Order> findPendingOrders(@Param("status") OrderStatus status);

  @Query(value = "select last_value + 1 from orders.order_id_seq", nativeQuery = true)
  Long getLastValue();
}
