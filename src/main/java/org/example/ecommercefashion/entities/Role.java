package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", unique = true, nullable = false)
  private String name;

  @ManyToMany(mappedBy = "roles")
  private Set<User> users;

  @ManyToMany
  @JoinTable(
      name = "role_permission",
      joinColumns = @JoinColumn(name = "id_role"),
      inverseJoinColumns = @JoinColumn(name = "id_permission"))
  @JsonIgnore
  private Set<Permission> permissions;
}
