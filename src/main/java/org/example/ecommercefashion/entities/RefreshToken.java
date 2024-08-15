package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Timestamp;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "refresh_tokens")
@Where(clause = "is_revoked = false")
@SuppressWarnings("unused")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "token")
  private String token;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @NotFound(action = NotFoundAction.IGNORE)
  private User user;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Timestamp createdAt;

  @Column(name = "is_revoked")
  private Boolean revoked;
}
