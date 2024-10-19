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

    @Query(value = "SELECT p.id, p.code , p.name , p.description ,p.create_at, p.update_at , p.create_by , p.update_by ,p.deleted, " +
            "p.id_brand, p.id_origin, p.id_material, p.id_category" +
            "            FROM products.product p " +
            "            JOIN products.brand b ON p.id_brand = b.id " +
            "            JOIN products.category c ON p.id_category = c.id " +
            "            JOIN products.material m ON p.id_material = m.id " +
            "            JOIN products.origin o ON p.id_origin = o.id " +
            "WHERE "+
            "(CAST(:#{#param.keyword} AS text) IS NULL " +
            "OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%')) " +
            "OR LOWER(p.code) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%')) " +
            "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%')) " +
            "OR LOWER(m.name) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%')) " +
            "OR LOWER(o.name) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%'))) " +
//            "AND (:#{#param.startDate} IS NULL OR p.create_at >= :#{#param.startDate}) " +
//            "AND (:#{#param.endDate} IS NULL OR p.create_at <= :#{#param.endDate})" +
            "AND p.deleted = false ",
            nativeQuery = true)
    Page<Product> filterProduct(ProductParam param, Pageable pageable);
}
