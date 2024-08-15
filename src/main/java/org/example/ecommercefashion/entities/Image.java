package org.example.ecommercefashion.entities;


import jakarta.persistence.*;
import lombok.*;
import org.example.ecommercefashion.enums.TypeImage;
import org.hibernate.annotations.Where;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
@Table(name = "images")
public class Image extends BaseEntity {

    @Column(name = "url")
    private String url;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TypeImage type;

    @Column(name = "size")
    private Long size;

    @Column(name = "title")
    private String title;


}
 