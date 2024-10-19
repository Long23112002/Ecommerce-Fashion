package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product", schema = "products")
@Where(clause = "deleted = false")
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

    @Column(name = "create_by",updatable = false)
    private Long createBy;

    @Column(name = "update_by")
    private Long updateBy;

    @Column(nullable = false)
    private Boolean deleted = false;

    @PrePersist
    public void generateCode(){
        if(this.code == null || this.code.isEmpty()){
            this.code = UUID.randomUUID().toString();
        }
    }

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "product")
    private List<ProductDetail> productDetails;

    @ManyToOne
//            (cascade = {
//            CascadeType.DETACH, CascadeType.MERGE,
//            CascadeType.PERSIST, CascadeType.REFRESH},
//            fetch = FetchType.LAZY)
    @JoinColumn(name = "id_brand", nullable = false)
    private Brand brand;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "id_origin")
    private Origin origin;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "id_material")
    private Material material;

    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category")
    private Category category;

}
