package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.PromotionParam;
import org.example.ecommercefashion.entities.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    @Query(
            "SELECT p FROM Promotion p WHERE " +
                    "(:#{#param.startDate} IS NULL OR p.startDate >= CAST(:#{#param.startDate} AS timestamp)) AND " +
                    "(:#{#param.endDate} IS NULL OR p.endDate <= CAST(:#{#param.endDate} AS timestamp)) AND " +
                    "(:#{#param.typePromotionEnum} IS NULL OR p.typePromotionEnum = :#{#param.typePromotionEnum}) AND " +
                    "(:#{#param.statusPromotionEnum} IS NULL OR p.statusPromotionEnum = :#{#param.statusPromotionEnum}) AND " +
                    "(:#{#param.valueMin} IS NULL OR p.value >= :#{#param.valueMin}) AND " +
                    "(:#{#param.valueMax} IS NULL OR p.value <= :#{#param.valueMax})"
    )
    Page<Promotion> filterPromotion(PromotionParam param, Pageable pageable);

    @Query("SELECT p FROM Promotion p WHERE p.deleted = false AND " +
            "(:startDate BETWEEN p.startDate AND p.endDate OR " +
            ":endDate BETWEEN p.startDate AND p.endDate OR " +
            "p.startDate BETWEEN :startDate AND :endDate)")
    List<Promotion> findOverlappingPromotions(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

}
