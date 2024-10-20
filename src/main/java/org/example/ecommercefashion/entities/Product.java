package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product", schema = "products")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "code")
  private String code;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

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

  @Column(name = "update_by")
  private Long updateBy;

  @Column(nullable = false)
  private Boolean deleted = false;

  @PrePersist
  public void generateCode() {
    if (this.code == null || this.code.isEmpty()) {
      this.code = UUID.randomUUID().toString();
    }
  }

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private List<ProductDetail> productDetails;

  @ManyToOne(
      cascade = {
        CascadeType.DETACH, CascadeType.MERGE,
        CascadeType.PERSIST, CascadeType.REFRESH
      },
      fetch = FetchType.LAZY)
  @JoinColumn(name = "id_brand", nullable = false)
  @JsonBackReference
  private Brand brand;

  @ManyToOne(
      cascade = {
        CascadeType.DETACH, CascadeType.MERGE,
        CascadeType.PERSIST, CascadeType.REFRESH
      },
      fetch = FetchType.LAZY)
  @JoinColumn(name = "id_origin")
  @JsonBackReference
  private Origin origin;

  @ManyToOne(
      cascade = {
        CascadeType.DETACH, CascadeType.MERGE,
        CascadeType.PERSIST, CascadeType.REFRESH
      },
      fetch = FetchType.LAZY)
  @JoinColumn(name = "id_material")
  @JsonBackReference
  private Material material;

  @ManyToOne(
      cascade = {
        CascadeType.DETACH, CascadeType.MERGE,
        CascadeType.PERSIST, CascadeType.REFRESH
      },
      fetch = FetchType.LAZY)
  @JsonIgnore
  @JoinColumn(name = "id_category")
  private Category category;
}
