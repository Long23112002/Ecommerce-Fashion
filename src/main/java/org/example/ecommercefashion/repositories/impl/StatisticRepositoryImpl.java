package org.example.ecommercefashion.repositories.impl;

import org.example.ecommercefashion.dtos.request.PageableRequest;
import org.example.ecommercefashion.repositories.StatisticRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class StatisticRepositoryImpl implements StatisticRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getCurrentDayRevenue() {
        String query_string = """
                    WITH all_days AS (
                        SELECT CURRENT_DATE AS day
                        UNION ALL
                        SELECT CURRENT_DATE - INTERVAL '1 day'
                    )
                    SELECT
                        to_char(all_days.day, 'DD/MM/YYYY'),
                        COALESCE(SUM(o.total_money - o.discount_amount), 0) AS total_revenue
                    FROM
                        all_days
                    LEFT JOIN
                        orders.orders_vn_time o
                        ON CAST(o.vn_success_at AS DATE) = all_days.day
                        AND o.status = 'SUCCESS'
                        AND o.deleted = false
                    GROUP BY
                        all_days.day
                    ORDER BY
                        all_days.day
                """;
        Query query = entityManager.createNativeQuery(query_string);
        List<Object[]> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<Object[]> getYearRevenueData(int year) {
        String query_string = """
                    WITH all_months AS (
                        SELECT GENERATE_SERIES(
                            DATE_TRUNC('year', TO_DATE(CAST(:year as varchar), 'YYYY')),
                            DATE_TRUNC('year', TO_DATE(CAST(:year as varchar), 'YYYY')) + INTERVAL '11 months',
                            INTERVAL '1 month'
                        ) AS month
                    )
                    SELECT
                        TO_CHAR(all_months.month, 'Month') AS name,
                        COALESCE(SUM(o.total_money - o.discount_amount), 0) AS total_revenue
                    FROM
                        all_months
                    LEFT JOIN
                        orders.order o ON DATE_TRUNC('month', o.success_at) = all_months.month
                        AND DATE_PART('year', o.success_at) = :year
                        AND o.status = 'SUCCESS'
                        AND o.deleted = false
                    GROUP BY
                        all_months.month
                    ORDER BY
                        all_months.month
                """;
        Query query = entityManager.createNativeQuery(query_string);
        query.setParameter("year", year);
        List<Object[]> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<Object[]> getMonthRevenueData(int year, int month) {
        String query_string = """
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
                    COALESCE(SUM(o.total_money - o.discount_amount), 0) AS revenue
                FROM
                    all_days
                LEFT JOIN orders.orders_vn_time o
                ON DATE(o.vn_success_at) = all_days.day
                   AND DATE_PART('year', o.vn_success_at) = :year
                   AND DATE_PART('month', o.vn_success_at) = :month
                   AND o.status = 'SUCCESS'
                   AND o.deleted = false
                GROUP BY
                    all_days.day
                ORDER BY
                    all_days.day;
                """;
        Query query = entityManager.createNativeQuery(query_string);
        query.setParameter("month", month);
        query.setParameter("year", year);
        List<Object[]> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public List<Object[]> getSoldProducts(int year, int month) {
        String query_string = """
                    WITH product_details AS (
                        SELECT
                            pd2.id AS id,
                            s.name AS size,
                            c.name AS color,
                            sum(od2.quantity) AS sold
                        FROM orders.order_detail od2
                        JOIN products.product_detail pd2
                            ON od2.product_detail_id = pd2.id
                        JOIN products.color c
                            ON pd2.id_color = c.id
                        JOIN products.size s 
                            ON pd2.id_size = s.id
                        JOIN orders.orders_vn_time o2 
                            ON o2.id = od2.order_id
                        WHERE
                            o2.status = 'SUCCESS'
                            AND o2.deleted = false
                            AND EXTRACT(MONTH FROM o2.vn_success_at) = :month
                            AND EXTRACT(YEAR FROM o2.vn_success_at) = :year
                        GROUP BY
                            pd2.id, s.name, c.name
                    )
                    SELECT
                        p.id,
                        p.name,
                        sum(od.quantity) AS total,
                        CAST(jsonb_agg(
                            DISTINCT jsonb_build_object(
                                'id', pd_details.id,
                                'size', pd_details.size,
                                'color', pd_details.color,
                                'sold', pd_details.sold
                            )
                        ) AS TEXT) AS product_details
                    FROM orders.orders_vn_time o
                    JOIN orders.order_detail od
                        ON o.id = od.order_id
                        AND od.deleted = false
                    JOIN products.product_detail pd
                        ON od.product_detail_id = pd.id
                    JOIN products.product p
                        ON p.id = pd.id_product
                    JOIN product_details pd_details
                        ON pd.id = pd_details.id
                    WHERE
                        o.status = 'SUCCESS'
                        AND o.deleted = false
                        AND EXTRACT(MONTH FROM o.vn_success_at) = :month
                        AND EXTRACT(YEAR FROM o.vn_success_at) = :year
                    GROUP BY
                        p.id
                    ORDER BY
                        total DESC;
                """;
        Query query = entityManager.createNativeQuery(query_string);
        query.setParameter("month", month);
        query.setParameter("year", year);
        List<Object[]> resultList = query.getResultList();
        return resultList;
    }

    @Override
    public Page<Object[]> getInventoryProduct(PageableRequest pageable) {
        String query_string = """
                SELECT
                	p.id,
                	p.name,
                	SUM(pd.quantity) AS quantity,
                	CAST(
                		jsonb_agg(
                			jsonb_build_object(
                				'id', pd.id,
                				'color', c.name,
                				'size', s.name,
                				'quantity', pd.quantity
                			)
                		) AS TEXT
                	) AS product_details
                FROM products.product p
                JOIN products.product_detail pd
                	ON p.id = pd.id_product
                    AND pd.deleted = false
                JOIN products.color c
                	ON c.id = pd.id_color
                JOIN products.size s
                	ON s.id = pd.id_size
                WHERE p.deleted = false
                GROUP BY p.id
                ORDER BY quantity :direction 
                """;
        query_string = query_string.replace(":direction", pageable.getSort().name());
        Query query = entityManager.createNativeQuery(query_string);
        int total = query.getResultList().size();
        int size = pageable.getSize();
        int page = pageable.getPage();
        query.setMaxResults(size);
        query.setFirstResult(page * size);
        List<Object[]> response = query.getResultList();
        return new PageImpl<>(response, pageable.toPageable(), total);
    }
}
