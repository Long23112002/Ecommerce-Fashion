package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Brand;
import org.example.ecommercefashion.entities.Category;
import org.example.ecommercefashion.entities.Material;
import org.example.ecommercefashion.entities.Origin;
import org.example.ecommercefashion.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  Boolean existsByNameIgnoreCase(String name);

  Boolean existsByMaterial(Material material);

  Boolean existsByBrand(Brand brand);

  Boolean existsByCategory(Category category);

  Boolean existsByOrigin(Origin origin);

  @Query(value = "select last_value + 1 from products.product_id_seq", nativeQuery = true)
  Long getLastValue();

  boolean existsByNameAndIdNot(String name, Long id);
}
