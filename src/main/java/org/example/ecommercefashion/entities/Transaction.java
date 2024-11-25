package org.example.ecommercefashion.entities;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "tb_transactions" , schema = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "gateway", nullable = false, length = 100)
  private String gateway;

  @Column(name = "transaction_date", nullable = false)
  private ZonedDateTime transactionDate;

  @Column(name = "account_number", length = 100)
  private String accountNumber;

  @Column(name = "sub_account", length = 250)
  private String subAccount;

  @Column(name = "amount_in")
  private BigDecimal amountIn = BigDecimal.ZERO;

  @Column(name = "amount_out")
  private BigDecimal amountOut = BigDecimal.ZERO;

  @Column(name = "accumulated")
  private BigDecimal accumulated = BigDecimal.ZERO;

  @Column(name = "code")
  private String code;

  @Column(name = "transaction_content")
  private String transactionContent;

  @Column(name = "reference_number")
  private String referenceNumber;

  @Column(name = "body")
  private String body;

}
