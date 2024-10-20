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

    @Query("SELECT p FROM ProductDetail p "
            + "WHERE "
            + "(:#{#param.idColor} IS NULL OR p.color.id = :#{#param.idColor}) AND "
            + "(:#{#param.idProduct} IS NULL OR p.product.id = :#{#param.idProduct}) AND "
            + "(:#{#param.minPrice} IS NULL OR (p.price IS NOT NULL AND p.price >= :#{#param.minPrice})) AND "
            + "(:#{#param.maxPrice} IS NULL OR p.price <= :#{#param.maxPrice}) AND "
            + "(:#{#param.idSize} IS NULL OR p.size.id = :#{#param.idSize})")
    Page<ProductDetail> filterProductDetail(ProductDetailParam param, Pageable pageable);
}