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
            (:#{#params.type} IS NULL OR CAST(d.type AS text) = CAST(:#{#params.type} AS text))
            AND (:#{#params.status} IS NULL OR CAST(d.discount_status AS text) = CAST(:#{#params.status} AS text))
            AND (:#{#params.name} IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :#{#params.name}, '%')))
            AND (
                (:#{#params.idProductDetail}) IS NULL
                OR jsonb_array_length(CAST(d.condition AS jsonb) -> 'idProductDetail') = 0
                OR ((SELECT bool_and(CAST(elem AS BIGINT) IN (:#{#params.idProductDetail == null ? #params.defaultList : #params.idProductDetail}))
                     FROM jsonb_array_elements(CAST(d.condition AS jsonb) -> 'idProductDetail') AS elem) = true)
            )
            AND (
                :#{#params.prices} IS NULL
                OR CAST(d.condition ->> 'price' AS DOUBLE PRECISION) IS NULL
                OR CAST(d.condition ->> 'price' AS DOUBLE PRECISION) <= CAST(CAST(:#{#params.prices} as varchar) as DOUBLE PRECISION)
            )
            AND d.deleted = false
        """, nativeQuery = true)
    Page<Discount> getFilterDiscountPage(@Param("params") DiscountParam params,
                                         Pageable pageable);


    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, long id);

    @Query(value = "select last_value + 1 from discounts.discount_id_seq", nativeQuery = true)
    Long getLastValue();
}
