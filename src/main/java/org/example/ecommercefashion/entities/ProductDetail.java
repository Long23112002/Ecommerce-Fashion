package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.*;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.value.UserValue;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_detail", schema = "products")
@Where(clause = "deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class ProductDetail {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "price")
  private Double price;

  @Column(name = "origin_price")
  private Double originPrice;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "images", columnDefinition = "jsonb")
  @Type(type = "jsonb")
  private List<File> images;

  @Column(name = "create_at", updatable = false)
  @CreationTimestamp
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp createAt;

  @Column(name = "update_at")
  @UpdateTimestamp
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp updateAt;

  @Column(name = "create_by", updatable = false)
  private Long createBy;

  @Transient private UserValue createByUser;

  @Transient private UserValue updateByUser;

  @Column(name = "update_by")
  private Long updateBy;

  @Column(nullable = false)
  private Boolean deleted = false;

  @ManyToOne(
      cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
      fetch = FetchType.LAZY)
  @JsonIgnoreProperties({"productDetails"})
  //  @JsonBackReference
  @Fetch(FetchMode.JOIN)
  @JoinColumn(name = "id_product")
  private Product product;

  @ManyToOne(
      cascade = {
        CascadeType.DETACH, CascadeType.MERGE,
        CascadeType.PERSIST, CascadeType.REFRESH
      },
      fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  @JoinColumn(name = "id_size")
  private Size size;

  @ManyToOne(
      cascade = {
        CascadeType.DETACH, CascadeType.MERGE,
        CascadeType.PERSIST, CascadeType.REFRESH
      },
      fetch = FetchType.LAZY)
  @Fetch(FetchMode.JOIN)
  @JoinColumn(name = "id_color")
  private Color color;

  @ManyToMany(mappedBy = "productDetailList", fetch = FetchType.LAZY)
  @BatchSize(size = 100)
  @JsonBackReference
  private List<Promotion> promotionList;

  @Transient
  @JsonIgnoreProperties("productDetailList")
  private Promotion promotion;
}
