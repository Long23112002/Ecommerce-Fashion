package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.OriginParam;
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Origin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginRepository extends JpaRepository<Origin,Long> {
    @Query("SELECT o from Origin o WHERE (:#{#param.name} is null or LOWER(o.name) like LOWER(concat( '%',:#{#param.name},'%')))")
    Page<Origin> FilterOrigin (OriginParam param, Pageable pageable);
}
