package org.example.ecommercefashion.repositories;

import java.util.Collection;
import java.util.List;
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

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
  Boolean existsProductDetailByColorAndProductAndSize(Product product, Color color, Size size);

//  @Query(
//      "SELECT p FROM ProductDetail p "
//          + "WHERE "
//          + "(:#{#param.keyword} IS NULL OR LOWER(p.size.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%')) "
//          + "AND (:#{#param.keyword} IS NULL OR LOWER(p.color.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%')) "
//          + "AND (:#{#param.keyword} IS NULL OR LOWER(p.product.name) LIKE CONCAT('%', LOWER(CAST(:#{#param.keyword} AS string)), '%')) "
//          + "AND (:#{#param.idColor} IS NULL OR p.color.id = :#{#param.idColor}) "
//          + "AND (:#{#param.idProduct} IS NULL OR p.product.id = :#{#param.idProduct}) "
//          + "AND (:#{#param.minPrice} IS NULL OR (p.price IS NOT NULL AND p.price >= :#{#param.minPrice})) "
//          + "AND (:#{#param.maxPrice} IS NULL OR p.price <= :#{#param.maxPrice}) "
//          + "AND (:#{#param.idSize} IS NULL OR p.size.id = :#{#param.idSize}) "
//          + "ORDER BY p.id DESC")
//  Page<ProductDetail> filterProductDetail(ProductDetailParam param, Pageable pageable);

  Boolean existsByColor(Color color);

  Boolean existsBySize(Size size);

  @Query("SELECT pd FROM ProductDetail pd JOIN pd.promotionList p WHERE p.id = :promotionId")
  Page<ProductDetail> findByPromotionId(@Param("promotionId") Long promotionId, Pageable pageable);

  @Query(
      "SELECT pd "
          + "FROM ProductDetail pd "
          + "WHERE pd.product.id = :idProduct AND pd.deleted = false")
  List<ProductDetail> getDetailByIdProduct(Long idProduct);

  Page<ProductDetail> findAllByProductId(Long idProduct, Pageable pageable);

  Boolean existsByProduct(Product product);

  @Query("SELECT SUM(p.price) FROM ProductDetail p WHERE p.id IN :productDetailIds")
  Double calculateTotalPriceByIds(@Param("productDetailIds") List<Long> productDetailIds);
}
