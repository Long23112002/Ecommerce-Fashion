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
      "SELECT p FROM Product p WHERE "
          + " (:#{#param.keyword} IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%')) "
          + "AND  :#{#param.code} is null or  LOWER(p.code) LIKE CONCAT('%' , LOWER(CAST( :#{#param.code} as string) ) ,  '%' ) "
          + "AND  :#{#param.keyword} is null or  LOWER(p.brand.name) LIKE CONCAT('%' , LOWER(CAST( :#{#param.keyword} as string) ) ,  '%' )"
          + "AND  :#{#param.keyword} is null or  LOWER(p.material.name) LIKE CONCAT('%' , LOWER(CAST( :#{#param.keyword} as string) ) ,  '%' )"
          + "AND  :#{#param.keyword} is null or  LOWER(p.category.name) LIKE CONCAT('%' , LOWER(CAST( :#{#param.keyword} as string) ) ,  '%' )"
          + "AND  :#{#param.keyword} is null or  LOWER(p.origin.name) LIKE CONCAT('%' , LOWER(CAST( :#{#param.keyword} as string) ) ,  '%' )"
          + "AND  :#{#param.idBrand} is null or  p.brand.id = :#{#param.idBrand}  "
          + "AND  :#{#param.idMaterial} is null or  p.material.id = :#{#param.idMaterial} "
          + "AND  :#{#param.idCategory} is null or  p.category.id = :#{#param.idCategory} "
          + "AND  :#{#param.idOrigin} is null or  p.origin.id = :#{#param.idOrigin}")
  Page<Product> filterProduct(ProductParam param, Pageable pageable);

  Boolean existsByMaterial(Material material);

  Boolean existsByBrand(Brand brand);

  Boolean existsByCategory(Category category);

  Boolean existsByOrigin(Origin origin);
}
