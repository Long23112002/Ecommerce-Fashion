package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ecommercefashion.enums.StatusDiscount;
import org.example.ecommercefashion.enums.TypeDiscount;
import org.example.ecommercefashion.model.Condition;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "discount", schema = "discounts")
@Where(clause = "deleted = false")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", updatable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID code;

    @Type(type = "json")
    @Column(name = "condition", columnDefinition = "json")
    private Condition condition;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private TypeDiscount type;

    @Column(name = "value")
    private Double value;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "discount_status")
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private StatusDiscount discountStatus;

    @Column(name = "create_at", updatable = false)
    @CreationTimestamp
    private Timestamp createAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private Timestamp updateAt;

    @Column(name = "create_by", updatable = false)
    private Long createBy;

    @Column(name = "update_by")
    private Long updateBy;

    @Column(name = "deleted")
    private Boolean deleted;

    @OneToMany(mappedBy = "discount")
    @JsonIgnoreProperties("discount")
    private List<Voucher> voucher;
}
