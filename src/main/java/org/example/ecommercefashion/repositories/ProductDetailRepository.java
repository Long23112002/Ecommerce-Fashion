package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.dtos.filter.ProductDetailParam;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    Boolean existsProductDetailByColorAndProductAndSize(Product product, Color color, Size size);

    @Query(value = "SELECT pd.id, pd.price, pd.quantity, pd.images , pd.create_at , pd.create_by , pd.update_by ,pd.deleted, " +
            "            pd.id_product , pd.id_size, pd.id_color " +
            "                        FROM products.product_detail pd  " +
            "                        JOIN products.size s ON s.id = pd.id_size " +
            "                        JOIN products.color c ON c.id = pd.id_color  " +
            "                        JOIN products.product p ON p.id = pd.id_product " +
            "            WHERE  " +
            "            (CAST(:#{#param.keyword} AS text) IS NULL " +
            "            OR LOWER(s.name) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%'))  " +
            "            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%'))  " +
            "            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#param.keyword}, '%'))) " +
            "            AND CAST(:#{#param.minPrice} AS double precision)  IS NULL OR pd.price >= CAST(:#{#param.minPrice} AS double precision) " +
            "            AND CAST(:#{#param.maxPrice} AS double precision) IS NULL OR pd.price <= CAST(:#{#param.maxPrice} AS double precision)" +
            "            AND p.deleted = false ", nativeQuery = true)
    Page<ProductDetail> filterProductDetail(ProductDetailParam param, Pageable pageable);
}
