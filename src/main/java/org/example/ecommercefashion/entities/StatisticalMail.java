package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "statistical", schema = "emails")
public class StatisticalMail {
    @Id
    @Column(name = "email_id")
    private Long emailId;

    @Column(name = "process_ids", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private List<FailedProcess> failedProcessIds;

    @ManyToOne
    @JoinColumn(name = "email_id", insertable = false, updatable = false)
    @JsonIgnore
    private Email email;
}
