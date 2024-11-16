package org.example.ecommercefashion.dtos.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.Color;
import org.example.ecommercefashion.entities.File;
import org.example.ecommercefashion.entities.Product;
import org.example.ecommercefashion.entities.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailCartResponse {

  private Long id;

  private Double price;

  private List<File> images;

  private Product product;

  private Size size;

  private Color color;

  private Double originPrice;

  private Integer quantity;
}
