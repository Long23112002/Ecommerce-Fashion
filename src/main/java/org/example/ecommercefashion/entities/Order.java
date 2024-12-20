package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import lombok.*;
import org.example.ecommercefashion.annotations.ValidPhoneNumber;
import org.example.ecommercefashion.entities.value.Address;
import org.example.ecommercefashion.enums.OrderStatus;
import org.example.ecommercefashion.enums.PaymentMethodEnum;
import org.hibernate.annotations.*;

@Entity
@Table(name = "order", schema = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "deleted = false")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class  Order implements Serializable , Cloneable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "discount_id")
  private Long discountId;

  @JoinColumn(name = "user_id")
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnoreProperties({"password", "authorities"})
  private User user;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus status;

//  @JoinColumn(name = "payment_method_id", nullable = false)
//  @ManyToOne(fetch = FetchType.LAZY)
//  @NotFound(action = NotFoundAction.IGNORE)
//  private Payment paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_method")
  @Type(type = "pgsql_enum")
  private PaymentMethodEnum paymentMethod;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Type(type = "jsonb")
  @Column(name = "address", columnDefinition = "jsonb")
  private Address address;

  @Column(name = "shipdate")
  private Timestamp shipdate;

  @Column(name = "money_ship")
  private Double moneyShip = 0.0;

  @Column(name = "discount_amount")
  private Double discountAmount = 0.0;

  @Column(name = "note")
  private String note;

  @Column(name = "total_money")
  private Double totalMoney = 0.0;

  @JoinColumn(name = "updated_by")
  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @NotFound(action = NotFoundAction.IGNORE)
  private User updatedBy;

  @Column(name = "created_at", updatable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @CreationTimestamp
  private Timestamp createdAt;

  @Column(name = "updated_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  @UpdateTimestamp
  private Timestamp updatedAt;

  @Column(name = "success_at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp successAt;

  @Column(name = "deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "staff_id")
  private Long staffId;

  @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
  @OrderBy("id ASC")
  @JsonManagedReference
  private List<OrderDetail> orderDetails;

  @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
  @JsonManagedReference
  @JsonIgnoreProperties({"order" , "user"})
  private List<OrderLog> orderLogs;

    @Override
    public Order clone() {
        try {
            Order clone = (Order) super.clone();
            return clone;
        } catch (Exception e) {
      throw new RuntimeException(e);
        }
    }

  @Column(name = "code")
  private String code;
}
