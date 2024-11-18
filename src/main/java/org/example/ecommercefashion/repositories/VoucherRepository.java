package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.VoucherParam;
import org.example.ecommercefashion.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    @Query("SELECT s FROM Voucher s  WHERE " +
            "    (:#{#param.createAt} IS NULL OR CAST(s.createAt AS string) Like %:#{#param.createAt}%) " +
            "AND (:#{#param.createBy} IS NULL OR s.createBy = :#{#param.createBy}) " +
            "AND (:#{#param.discountId} IS NULL OR s.discount.id = :#{#param.discountId})")
    Page<Voucher> getFilterVoucherPage(@Param("param")VoucherParam param,
                                       Pageable pageable);
    Optional<Voucher> findByCode(String Code);

    boolean existsByDiscountId(Long id);

    @Query(value = "select last_value + 1 from discounts.voucher_id_seq", nativeQuery = true)
    Long getLastValue();
}
