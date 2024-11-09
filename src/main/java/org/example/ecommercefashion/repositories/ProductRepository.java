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
            + "(CAST(:#{#param.keyword} AS string) IS NULL OR lower(p.name) LIKE CONCAT('%', CAST(:#{#param.keyword} AS string), '%') "
            + " OR cast(lower(p.code) as string) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR cast(lower(p.description) as string) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR cast(lower(p.brand.name) as string) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%')"
            + " OR cast(lower(p.origin.name) as string) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR cast(lower(p.category.name) as string) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR cast(lower(p.material.name) as string) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%')) AND "
            + "(:#{#param.idBrand} IS NULL OR p.brand.id = :#{#param.idBrand}) AND "
            + "(:#{#param.idOrigin} IS NULL OR p.origin.id = :#{#param.idOrigin}) AND "
            + "(:#{#param.idCategory} IS NULL OR p.category.id = :#{#param.idCategory}) AND "
            + "(:#{#param.idMaterial} IS NULL OR p.material.id = :#{#param.idMaterial}) "
            + "ORDER BY p.id DESC ")
    Page<Product> filterProduct(ProductParam param, Pageable pageable);




  Boolean existsByMaterial(Material material);

  Boolean existsByBrand(Brand brand);

  Boolean existsByCategory(Category category);


    Boolean existsByCode(String code);

  Boolean existsByOrigin(Origin origin);

  @Query(value  = "select last_value + 1 from products.product_id_seq", nativeQuery = true)
  Long getLastValue();

}
