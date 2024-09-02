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
    @Query("select d from Discount d where (:id is NUll OR d.id = id)")
    Page<Discount> getDiscountPage(@Param("id") Long id, Pageable pageable);


}
