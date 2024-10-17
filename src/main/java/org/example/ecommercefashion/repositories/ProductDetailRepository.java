package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.ProductDetail;
import org.example.ecommercefashion.entities.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long> {
    Boolean existsProductDetailByColorAndProductAndSize(Product product, Color color, Size size);

    //   Page<ProductDetail> filterProductDetail(ProductDetailParam param, Pageable pageable);
}
