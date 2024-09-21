package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ecommercefashion.enums.email.EmailTypeEnum;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email", schema = "emails")
@Entity
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "subject")
    private String subject;
    @Column(name = "content")
    private String content;
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    @Column(name = "send_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Timestamp sendAt;
    @Column(name = "send_from")
    private String sendFrom;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @Type(type = "pgsql_enum")
    private EmailTypeEnum type;
    @Column(nullable = false, name = "is_deleted")
    private Boolean isDeleted = false;
    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<EmailSendLog> emailSendLogs;

    @OneToMany(mappedBy = "email", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<ProcessSend> processSends;
    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private Template template;

}
