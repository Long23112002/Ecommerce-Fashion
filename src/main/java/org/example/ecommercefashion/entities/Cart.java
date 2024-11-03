package org.example.ecommercefashion.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.sql.Timestamp;
import java.util.Set;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.value.CartValue;
import org.example.ecommercefashion.entities.value.CartValueInfo;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cart", schema = "carts")
@Where(clause = "is_deleted = false")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Type(type = "jsonb")
  @Column(name = "product_detail_carts", columnDefinition = "jsonb")
  private Set<CartValue> cartValues;

  @Transient private Set<CartValueInfo> cartValueInfos;

  @CreationTimestamp
  @Column(name = "create_at", updatable = false)
  private Timestamp createAt;

  @Column(name = "is_deleted")
  private Boolean isDeleted = false;
}
