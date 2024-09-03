package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query("SELECT s FROM Voucher s " +
            "WHERE (:id IS NULL OR s.id = :id) " +
            "AND (:createAt IS NULL OR CAST(s.createAt AS string) Like %:createAt%) " +
            "AND (:createBy IS NULL OR s.createBy = :createBy) " +
            "AND (:discountId IS NULL OR s.discount.id = :discountId)")
    Page<Voucher> getFilterVoucherPage(@Param("id") Long id,
                                       @Param("createAt") String createAt,
                                       @Param("createBy") Long createBy,
                                       @Param("discountId") Long discountId,
                                       Pageable pageable);

}
