package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.ProductParam;
import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.Material;
import org.example.ecommercefashion.entities.Origin;
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
            + " (lower(:#{#param.keyword}) LIKE '' "
            + " OR lower(p.name) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR lower(p.code) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR lower(p.description) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR lower(p.brand.name) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%')"
            + " OR lower(p.origin.name) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR lower(p.category.name) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR lower(p.material.name) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%'))"
            + " AND (:#{#param.idBrand} IS NULL OR p.brand.id = :#{#param.idBrand})"
            + " AND (:#{#param.idOrigin} IS NULL OR p.origin.id = :#{#param.idOrigin})"
            + " AND (:#{#param.idCategory} IS NULL OR p.category.id = :#{#param.idCategory})"
            + " AND (:#{#param.idMaterial} IS NULL OR p.material.id = :#{#param.idMaterial})"
            + " ORDER BY p.id DESC ")

    Page<Product> filterProduct(ProductParam param, Pageable pageable);

    Boolean existsByMaterial(Material material);

    Boolean existsByBrand(Brand brand);

    Boolean existsByCategory(Category category);

    Boolean existsByOrigin(Origin origin);


}
