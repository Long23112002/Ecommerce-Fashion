package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role" , schema = "users")
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

  @ManyToMany(mappedBy = "roles", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
  @JsonIgnore
  private Set<User> users;

  @ManyToMany
  @JoinTable(
      name = "role_permission",
      schema = "users",
      joinColumns = @JoinColumn(name = "id_role"),
      inverseJoinColumns = @JoinColumn(name = "id_permission"))
  @JsonIgnore
  private Set<Permission> permissions;
}
