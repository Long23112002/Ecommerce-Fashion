package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("select d from Discount d where (:type is NULL OR CAST(d.type AS string) LIKE %:type%) " +
            "and (:discountStatus is NULL OR CAST(d.discountStatus AS string) LIKE %:discountStatus%) " +
            "and (:name is NULL OR d.name LIKE %:name%)")
    Page<Discount> getFilterDiscountPage(@Param("type") String type,
                                         @Param("discountStatus") String discountStatus,
                                         @Param("name") String id,
                                         Pageable pageable);
}
