package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ecommercefashion.enums.GenderEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "user", schema = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Where(clause = "deleted = false")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class User implements UserDetails, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(nullable = false, unique = true, name = "email")
  private String email;

  @Column(name = "password")
  private String password;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "phone_number", unique = true)
  private String phoneNumber;

  @Column(name = "birth")
  private Date birth;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender")
  @Type(type = "pgsql_enum")
  private GenderEnum gender;

  @Column(columnDefinition = "TEXT", name = "avatar")
  private String avatar;

  @Column(name = "id_google_account")
  private String googleAccountId;

  @Column(name = "id_facebook_account")
  private String facebookAccountId;

  @Column(name = "create_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreationTimestamp
  private Date createAt;

  @Column(name = "update_at")
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  private Date updateAt;

  @Column(name = "slug_email")
  private String slugEmail;

  @Column(name = "slug_full_name")
  private String slugFullName;

  @Column(nullable = false, name = "is_admin")
  private Boolean isAdmin = false;

  @Column(nullable = false, name = "is_verified")
  private Boolean isVerified = false;

  @Column(nullable = false)
  private Boolean deleted = false;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      schema = "users",
      joinColumns = @JoinColumn(name = "id_user"),
      inverseJoinColumns = @JoinColumn(name = "id_role"))
  @JsonIgnore
  private Set<Role> roles;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities =
        roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toSet());

    if (Boolean.TRUE.equals(this.isAdmin)) {
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return isVerified;
  }
}
