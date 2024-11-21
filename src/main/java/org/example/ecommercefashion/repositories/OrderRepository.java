package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.OrderParam;
import org.example.ecommercefashion.entities.Order;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.entities.ProductDetail;
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

    @Query(value =
            """
                    WITH all_days AS (
                        SELECT CURRENT_DATE AS day
                        UNION ALL
                        SELECT CURRENT_DATE - INTERVAL '1 day'
                    )
                    SELECT
                        to_char(all_days.day, 'DD/MM/YYYY'),
                        COALESCE(SUM(o.final_price), 0) AS total_revenue
                    FROM
                        all_days
                    LEFT JOIN
                        orders.order o
                        ON CAST(o.updated_at AS DATE) = all_days.day
                        AND o.status = 'SUCCESS'
                        AND o.deleted = false
                    GROUP BY
                        all_days.day
                    ORDER BY
                        all_days.day
                    """, nativeQuery = true)
    List<Object[]> getCurrentDayRevenue();

    @Query(value =
            """
                    WITH all_months AS (
                        SELECT GENERATE_SERIES(
                            DATE_TRUNC('year', TO_DATE(CAST(:year as varchar), 'YYYY')),
                            DATE_TRUNC('year', TO_DATE(CAST(:year as varchar), 'YYYY')) + INTERVAL '11 months',
                            INTERVAL '1 month'
                        ) AS month
                    )
                    SELECT
                        TO_CHAR(all_months.month, 'Month') AS name,
                        COALESCE(SUM(o.final_price), 0) AS total_revenue
                    FROM
                        all_months
                    LEFT JOIN
                        orders.order o ON DATE_TRUNC('month', o.updated_at) = all_months.month
                        AND DATE_PART('year', o.updated_at) = :year
                        AND o.status = 'SUCCESS'
                        AND o.deleted = false
                    GROUP BY
                        all_months.month
                    ORDER BY
                        all_months.month
                    """, nativeQuery = true)
    List<Object[]> getYearRevenueData(int year);

    @Query(value =
            """
                    WITH selected_month AS (
                        SELECT
                            TO_DATE(:year || '-' || :month || '-01', 'YYYY-MM-DD') AS first_day,
                            TO_DATE(:year || '-' || :month || '-01', 'YYYY-MM-DD') + INTERVAL '1 month' - INTERVAL '1 day' AS last_day
                    ),
                    all_days AS (
                        SELECT GENERATE_SERIES(
                            (SELECT first_day FROM selected_month),
                            (SELECT last_day FROM selected_month),
                            INTERVAL '1 day'
                        ) AS day
                    )
                    SELECT
                        TO_CHAR(all_days.day, 'FMDD') AS day, 
                        COALESCE(SUM(o.final_price), 0) AS revenue 
                    FROM
                        all_days
                    LEFT JOIN
                        orders.order o
                        ON DATE(o.updated_at) = all_days.day
                        AND DATE_PART('year', o.updated_at) = :year
                        AND DATE_PART('month', o.updated_at) = :month
                        AND o.status = 'SUCCESS'
                        AND o.deleted = false
                    GROUP BY
                        all_days.day
                    ORDER BY
                        all_days.day
                    """, nativeQuery = true)
    List<Object[]> getMonthRevenueData(int year, int month);

    @Query(value =
            """
                    SELECT 
                    	p.id,
                    	p.name,
                    	sum(od.quantity) as total
                    FROM orders.order o
                    JOIN orders.order_detail od
                    ON o.id = od.order_id
                    JOIN products.product_detail pd
                    ON od.product_detail_id = pd.id
                    JOIN products.product p
                    ON p.id = pd.id_product
                    WHERE 
                    	o.status = 'SUCCESS'
                        AND o.deleted = false
                        AND EXTRACT(MONTH FROM o.updated_at) = :month
                        AND EXTRACT(YEAR FROM o.updated_at) = :year
                    GROUP BY
                    	p.id
                    ORDER BY
                    	total DESC
                    """, nativeQuery = true)
    List<Object[]> getSoldProducts(int year, int month);

}
