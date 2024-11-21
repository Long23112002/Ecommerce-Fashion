package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.DiscountParam;
import org.example.ecommercefashion.entities.Discount;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query(value = """
            SELECT *
            FROM discounts.discount d
            WHERE
                (:type IS NULL OR CAST(d.type AS text) = CAST(:type AS text))
                AND (:status IS NULL OR CAST(d.discount_status AS text) = CAST(:status AS text))
                AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))\s
                AND (
                    (
                        SELECT bool_and(CAST(elem AS BIGINT) IN (:idProductDetail))
                        FROM jsonb_array_elements_text(CAST(d.condition AS jsonb) -> 'idProductDetail') AS elem
                    ) = true
                    OR jsonb_array_length(CAST(d.condition AS jsonb) -> 'idProductDetail') = 0
                )
                AND (
                    CAST(d.condition ->> 'price' AS DOUBLE PRECISION) IS NULL
                    OR CAST(d.condition ->> 'price' AS DOUBLE PRECISION) <= :prices
                )
            	AND d.deleted = false
                        """, nativeQuery = true)
    Page<Discount> getFilterDiscountPage(@Param("type") TypeDiscount type,
                                         @Param("status") StatusDiscount status,
                                         @Param("name") String name,
                                         @Param("idProductDetail") List<Long> idProductDetail,
                                         @Param("prices") Double prices,
                                         Pageable pageable);

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name , long id);

    @Query(value = "select last_value + 1 from discounts.discount_id_seq", nativeQuery = true)
    Long getLastValue();
}
