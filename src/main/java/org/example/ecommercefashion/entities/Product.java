package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.entities.value.UserValue;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product", schema = "products")
@Where(clause = "deleted = false")
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false)
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

    @Transient
    private UserValue createByUser;

    @Column(name = "update_by")
    private Long updateBy;

    @Transient
    private UserValue updateByUser;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "image")
    private String image;

    @Column(name = "min_price")
    private Long minPrice;

    @Column(name = "max_price")
    private Long maxPrice;

    //    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    //    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    //    @JsonManagedReference
    ////    @JsonBackReference
    //    private List<ProductDetail> productDetails;

    //  @PrePersist
    //  public void generateCode() {
    //    if (this.code == null || this.code.isEmpty()) {
    //      this.code = UUID.randomUUID().toString();
    //    }
    //  }

    @Transient
    private Promotion promotion;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "product"})
    private List<ProductDetail> productDetails;


    @ManyToOne(
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            },
            fetch = FetchType.LAZY)
    @JoinColumn(name = "id_brand", nullable = false)
    //    @JsonBackReference
    @Fetch(FetchMode.JOIN)
    private Brand brand;

    @ManyToOne(
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            },
            fetch = FetchType.LAZY)
    @JoinColumn(name = "id_origin")
    //    @JsonBackReference
    @Fetch(FetchMode.JOIN)
    private Origin origin;

    @ManyToOne(
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            },
            fetch = FetchType.LAZY)
    @JoinColumn(name = "id_material")
    //    @JsonBackReference
    @Fetch(FetchMode.JOIN)
    private Material material;

    @ManyToOne(
            cascade = {
                    CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH
            },
            fetch = FetchType.LAZY)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "id_category")
    @JsonIgnoreProperties({"products"})
    //    @JsonBackReference
    private Category category;
}