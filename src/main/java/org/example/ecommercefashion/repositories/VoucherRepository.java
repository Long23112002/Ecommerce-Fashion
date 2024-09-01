package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.response.VoucherResponse;
import org.example.ecommercefashion.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    @Query(value = "SELECT v.id as id, v.code as code, v.create_at as createAt, v.update_at as updateAt, " +
            "v.used_at as usedAt, v.create_by as createBy, v.update_by as updateBy, v.used_by as usedBy, " +
            "v.deleted as deleted, v.id_discount as idDiscount " +
            "FROM discounts.voucher v " +
            "JOIN discounts.discount d ON v.id_discount = d.id",
            nativeQuery = true)
    Page<VoucherResponse> getAll(Pageable pageable);
}
