package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.BrandParam;
import org.example.ecommercefashion.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository {
    @Query("select b from Brand b WHERE (:#{#param.name} is null or b.name like %:#{#param.name}%)")
    Page<Brand> FilterBrand(BrandParam param, Pageable pageable);

}
