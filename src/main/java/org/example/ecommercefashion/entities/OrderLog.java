package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.apache.poi.ss.formula.functions.T;
import org.example.ecommercefashion.enums.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "order_log", schema = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class OrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "old_value")
    @Enumerated(EnumType.STRING)
    private OrderStatus oldStatus;

    @JoinColumn(name = "order_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("orderLogs")
    private Order order;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "new_value")
    @Enumerated(EnumType.STRING)
    private OrderStatus newValue;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

}
