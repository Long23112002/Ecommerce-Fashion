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
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    Boolean existsProductDetailByColorAndProductAndSize(Product product, Color color, Size size);

    @Query("SELECT p FROM ProductDetail p "
            + "WHERE "
            + "(lower(:#{#param.keyword}) IS NULL OR lower(p.size.name) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR lower(p.color.name) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%') "
            + " OR lower(p.product.name) LIKE CONCAT('%', CAST(lower(:#{#param.keyword}) AS string), '%')) AND "
            + "(:#{#param.idColor} IS NULL OR p.color.id = :#{#param.idColor}) AND "
            + "(:#{#param.idProduct} IS NULL OR p.product.id = :#{#param.idProduct}) AND "
            + "(:#{#param.minPrice} IS NULL OR (p.price IS NOT NULL AND p.price >= :#{#param.minPrice})) AND "
            + "(:#{#param.maxPrice} IS NULL OR p.price <= :#{#param.maxPrice}) AND "
            + "(:#{#param.idSize} IS NULL OR p.size.id = :#{#param.idSize})"
            + "ORDER BY p.id DESC ")
    Page<ProductDetail> filterProductDetail(ProductDetailParam param, Pageable pageable);

    Boolean existsByColor(Color color);

    Boolean existsBySize(Size size);

    @Query("SELECT pd FROM ProductDetail pd JOIN pd.promotionList p WHERE p.id = :promotionId")
    Page<ProductDetail> findByPromotionId(@Param("promotionId") Long promotionId, Pageable pageable);

    @Query("SELECT pd " +
            "FROM Product p " +
            "JOIN ProductDetail pd ON p.id = pd.product.id " +
            "WHERE p.id = :idProduct " +
            "GROUP BY p.id, pd.id")
    List<ProductDetail> getDetailByIdProduct(Long idProduct);
}
