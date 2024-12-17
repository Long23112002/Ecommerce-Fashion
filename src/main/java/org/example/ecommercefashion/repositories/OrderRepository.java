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

    @Query(value = "SELECT p.* " +
            "FROM orders.order p " +
            "LEFT JOIN users.user u ON p.user_id = u.id " +
            "WHERE p.status <> 'DRAFT' " +
            "AND (:#{#param.userId} IS NULL OR p.user_id = CAST(:#{#param.userIdDefault} AS BIGINT)) " +
            "AND (:#{#param.status} IS NULL OR p.status = CAST(:#{#param.statusDefault.name()} AS VARCHAR)) " +
            "AND (:#{#param.phoneNumber} IS NULL OR p.phone_number = :#{#param.phoneNumberDefault}) " +
            "AND (:#{#param.keyword} IS NULL OR LOWER(u.full_name) LIKE CONCAT('%', LOWER(:#{#param.keywordDefault}), '%')) " +
            "AND (:#{#param.day} IS NULL OR EXTRACT('DAY' FROM (p.success_at + INTERVAL '7 hour')) = :#{#param.dayDefault}) " +
            "AND (:#{#param.month} IS NULL OR EXTRACT('MONTH' FROM (p.success_at + INTERVAL '7 hour')) = :#{#param.monthDefault}) " +
            "AND (:#{#param.year} IS NULL OR EXTRACT('YEAR' FROM (p.success_at + INTERVAL '7 hour')) = :#{#param.yearDefault}) " +
            "AND p.deleted = FALSE " +
            "ORDER BY p.updated_at DESC, p.created_at DESC", nativeQuery = true)
    Page<Order> filter(OrderParam param, Pageable pageable);

    @Query("SELECT COUNT(*) " +
            "FROM Order o " +
            "WHERE o.status = 'PENDING_AT_STORE' and o.deleted = false and o.staffId = :staffId ")
    Long countOrderPendingStore(Long staffId);

    @Query("SELECT o FROM Order o WHERE o.status = :status and o.staffId = :staffId ORDER BY o.createdAt DESC")
    List<Order> findPendingOrders(@Param("status") OrderStatus status, @Param("staffId") Long staffId);

    @Query(value = "select last_value + 1 from orders.order_id_seq", nativeQuery = true)
    Long getLastValue();

}
