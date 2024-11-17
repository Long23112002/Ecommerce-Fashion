package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

@Entity
@Table(name = "order_detail", schema = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "deleted = false")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class OrderDetail implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "order_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @NotFound(action = NotFoundAction.IGNORE)
  @JsonBackReference
  private Order order;

  @JoinColumn(name = "product_detail_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY)
  @NotFound(action = NotFoundAction.IGNORE)
  @JsonIgnoreProperties({"createByUser", "updateByUser"})
  private ProductDetail productDetail;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "price")
  private Double price = 0.0;

  @Column(name = "total_money")
  private Double totalMoney = 0.0;

  @Column(name = "created_at", updatable = false)
  @CreationTimestamp
  private Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private Timestamp updatedAt;

  @Column(name = "deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "created_by")
  private Long createdBy;

  @Column(name = "updated_by")
  private Long updatedBy;
}
