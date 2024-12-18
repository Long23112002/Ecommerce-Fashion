package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.Material;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByNameIgnoreCase(String name);

    Boolean existsByMaterial(Material material);

    Boolean existsByBrand(Brand brand);

    Boolean existsByCategory(Category category);

    Boolean existsByOrigin(Origin origin);

    @Query(value = "select last_value + 1 from products.product_id_seq", nativeQuery = true)
    Long getLastValue();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query(value = """
            SELECT p.*,
                   (
                       CASE WHEN p.id_category = :#{#product.category.id} AND c.deleted = false THEN 1 ELSE 0 END +
                       CASE WHEN p.id_origin = :#{#product.origin.id} AND o.deleted = false THEN 1 ELSE 0 END +
                       CASE WHEN p.id_brand = :#{#product.brand.id} AND b.deleted = false THEN 1 ELSE 0 END +
                       CASE WHEN p.id_material = :#{#product.material.id} AND m.deleted = false THEN 1 ELSE 0 END
                   ) as similar_point
            FROM products.product p
            JOIN products.category c ON p.id_category = c.id
            JOIN products.origin o ON p.id_origin = o.id
            JOIN products.brand b ON p.id_brand = b.id
            JOIN products.material m ON p.id_material = m.id
            JOIN products.product_detail pd ON p.id = pd.id_product
            WHERE 
                p.id <> :#{#product.id}
                AND p.deleted = false
                AND pd.deleted = false
            GROUP BY p.id, c.deleted, o.deleted, m.deleted, b.deleted
            HAVING COUNT(pd.id) > 0
            ORDER BY similar_point DESC
            """, nativeQuery = true)
    Page<Product> findSimilarProducts(Product product,
                                      Pageable pageable);

    @Query(value = """
            WITH ranked_products AS (
                SELECT
                    p.*,
                    COALESCE(COUNT(od.id), 0) AS total_order,
                    COALESCE(SUM(od.quantity), 0) AS total_quantity,
                    FLOOR(EXTRACT(EPOCH FROM (CURRENT_DATE - o.success_at)) / (7 * 86400)) AS week_group,
                    ROW_NUMBER() OVER (
                        PARTITION BY p.id
                        ORDER BY
                            FLOOR(EXTRACT(EPOCH FROM (CURRENT_DATE - o.success_at)) / (7 * 86400)) ASC,
                            COALESCE(SUM(od.quantity), 0) DESC
                    ) AS row_num
                FROM
                    products.product p
                INNER JOIN
                    products.product_detail pd
                    	ON pd.id_product = p.id
                    	and pd.deleted = false
                LEFT JOIN
                    orders.order_detail od
                        ON od.product_detail_id = pd.id
                        AND od.deleted = false
                LEFT JOIN
                    orders.order o
                        ON o.id = od.order_id
                       AND o.status = 'SUCCESS'
                       AND o.deleted = false
                WHERE
                    o.id IS NULL
                    OR o.success_at IS NOT NULL
                    AND p.deleted = false
                GROUP BY
                    p.id,
                    week_group
                HAVING
                	COUNT(pd.id) > 0
            )
            SELECT *
            FROM
                ranked_products rp
            WHERE
                rp.row_num = 1
            ORDER BY
                week_group ASC,
                rp.total_order DESC,
                rp.total_quantity DESC
            LIMIT :#{#pageable.pageSize}
            OFFSET :#{#pageable.offset}
            """, nativeQuery = true)
    List<Product> findHotProducts(Pageable pageable);

    @Query(value = """
            SELECT DISTINCT p
            FROM Product p
            JOIN p.productDetails pd
            JOIN pd.promotionList pl
            WHERE pl.statusPromotionEnum = 'ACTIVE'
            """)
    Page<Product> findProductInPromotion(Pageable pageable);

}
