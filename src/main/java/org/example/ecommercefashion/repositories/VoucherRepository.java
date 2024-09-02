package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query("SELECT s from  Voucher s where (:id is NULL OR s.id = :id)")
    Page<Voucher> getVoucherPage(@Param("id") Long id, Pageable pageable);
}
