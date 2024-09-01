package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.response.DiscountResponse;
import org.example.ecommercefashion.entities.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {
    @Query(value = "SELECT id AS id, code AS code, \"condition\" AS condition, \"type\" AS type, value AS value, " +
            "max_value AS maxValue, discount_status AS discountStatus, create_by AS createBy, " +
            "update_by AS updateBy, start_date AS startDate, end_date AS endDate, " +
            "create_at AS createAt, update_at AS updateAt, deleted AS deleted " +
            "FROM discount",
            nativeQuery = true)
    Page<DiscountResponse> getAll(Pageable pageable);

}
