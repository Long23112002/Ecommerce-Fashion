package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByNameIgnoreCase(String name);
    @Query("SELECT p FROM Product p "
            + "WHERE "
            + "(:#{#param.keyword} IS NULL OR p.name LIKE CONCAT('%', CAST(:#{#param.keyword} AS string), '%') "
            + " OR p.code LIKE CONCAT('%', CAST(:#{#param.keyword} AS string), '%') "
            + " OR p.description LIKE CONCAT('%', CAST(:#{#param.keyword} AS string), '%') "
            + " OR p.brand.name LIKE CONCAT('%', CAST(:#{#param.keyword} AS string), '%')"
            + " OR p.origin.name LIKE CONCAT('%', CAST(:#{#param.keyword} AS string), '%') "
            + " OR p.category.name LIKE CONCAT('%', CAST(:#{#param.keyword} AS string), '%') "
            + " OR p.material.name LIKE CONCAT('%', CAST(:#{#param.keyword} AS string), '%')) AND "
            + "(:#{#param.idBrand} IS NULL OR p.brand.id = :#{#param.idBrand}) AND "
            + "(:#{#param.idOrigin} IS NULL OR p.origin.id = :#{#param.idOrigin}) AND "
            + "(:#{#param.idCategory} IS NULL OR p.category.id = :#{#param.idCategory}) AND "
            + "(:#{#param.idMaterial} IS NULL OR p.material.id = :#{#param.idMaterial}) ")

    Page<Product> filterProduct(ProductParam param, Pageable pageable);


}
