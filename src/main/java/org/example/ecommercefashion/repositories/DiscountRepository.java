package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.DiscountParam;
import org.example.ecommercefashion.entities.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    @Query("select d from Discount d where (:#{#param.type} IS NULL OR d.type = :#{#param.type}) " +
            "and (:#{#param.status} is NULL OR d.discountStatus = :#{#param.status}) " +
            "and (:#{#param.status} is NULL OR d.name LIKE %:#{#param.name}%)")
    Page<Discount> getFilterDiscountPage(@Param("param")DiscountParam param,
                                         Pageable pageable);

}
