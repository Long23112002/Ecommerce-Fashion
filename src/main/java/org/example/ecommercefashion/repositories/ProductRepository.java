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

  @Query(
      "SELECT p FROM Product p WHERE (:#{#param.idMaterial} IS NULL OR p.material.id = :#{#param.idMaterial})"
          + "AND (:#{#param.idBrand} IS NULL OR p.brand.id = :#{#param.idBrand})"
          + "AND (:#{#param.idCategory} IS NULL OR p.category.id = :#{#param.idCategory})"
          + "AND (:#{#param.idOrigin} IS NULL OR p.origin.id = :#{#param.idOrigin})"
          + "AND (:#{#param.keyword} IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%'))"
          + "AND (:#{#param.code} IS NULL OR LOWER(p.code) LIKE CONCAT('%', LOWER(CAST(:#{#param.code} AS string)), '%'))"
          + "AND (:#{#param.keyword} IS NULL OR LOWER(p.brand.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%'))"
          + "AND (:#{#param.keyword} IS NULL OR LOWER(p.material.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%'))"
          + "AND (:#{#param.keyword} IS NULL OR LOWER(p.category.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%'))"
          + "AND (:#{#param.keyword} IS NULL OR LOWER(p.origin.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%'))"
          + "AND (:#{#param.minPrice} IS NULL OR p.minPrice >= :#{#param.minPrice})"
          + "AND (:#{#param.maxPrice} IS NULL OR p.maxPrice <= :#{#param.maxPrice})"
          + "AND (:#{#param.idColor} IS NULL OR p.id IN (SELECT pd.product.id FROM ProductDetail pd WHERE pd.color.id = :#{#param.idColor}))"
          + "AND (:#{#param.idSize} IS NULL OR p.id IN (SELECT pd.product.id FROM ProductDetail pd WHERE pd.size.id = :#{#param.idSize}))"
          + " ORDER BY p.id DESC ")
  Page<Product> filterProduct(ProductParam param, Pageable pageable);

  Boolean existsByMaterial(Material material);

  Boolean existsByBrand(Brand brand);

  Boolean existsByCategory(Category category);

  Boolean existsByOrigin(Origin origin);

  @Query(value = "select last_value + 1 from products.product_id_seq", nativeQuery = true)
  Long getLastValue();
}
