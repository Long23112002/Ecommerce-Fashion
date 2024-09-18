package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.BrandParam;
import org.example.ecommercefashion.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("SELECT o from Brand o WHERE (:#{#param.name} is null or LOWER(o.name) like LOWER(concat( '%',:#{#param.name},'%')))")
    Page<Brand> filterBrand(BrandParam param, Pageable pageable);

}
