package org.example.ecommercefashion.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "template",schema = "emails")
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "subject")
    private String subject;
    @Column(name = "html")
    private String html;
    @Column(name = "variables", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private List<String> variables;
    @Column(nullable = false, name = "is_deleted")
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Email> emails;
}
