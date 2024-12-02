package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.OrderDetail;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.entities.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT od FROM OrderDetail od WHERE od.order.id =:orderId")
    Page<OrderDetail> filter(@Param("orderId") Long orderId,Pageable pageable);

    @Query(value = "select last_value + 1 from orders.order_detail_id_seq", nativeQuery = true)
    Long getLastValue();
    Boolean existsByProductDetail(ProductDetail productDetail);

    @Query("SELECT od FROM OrderDetail od WHERE od.order.id =:orderId")
    List<OrderDetail> getAllByOrderId(@Param("orderId") Long orderId);

    Optional<OrderDetail> findOrderDetailByOrderAndProductDetail(Order order, ProductDetail productDetail);

    List<OrderDetail> findAllByOrder(Order order);

}
