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
    @Query(value = "SELECT * FROM products.brand o WHERE " +
            "(CAST(:#{#param.name} AS text) IS NULL OR LOWER(o.name) LIKE LOWER(CONCAT('%', :#{#param.name}, '%'))) AND o.deleted = false ",
            nativeQuery = true)
    Page<Brand> filterBrand(BrandParam param, Pageable pageable);

    Boolean existsByName(String name);
}
