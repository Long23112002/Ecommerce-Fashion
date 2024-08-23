package org.example.ecommercefashion.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ecommercefashion.enums.TypeImage;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "deleted = false")
@Table(name = "images" , schema = "images")
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
 