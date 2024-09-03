package org.example.ecommercefashion.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ecommercefashion.enums.email.EmailLogStatusEnum;
import org.example.ecommercefashion.enums.email.MailApiType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_send_log" , schema = "emails")
@Entity
public class EmailSendLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    @Column(name = "description")
    private String description;
    @Column(name = "mail_api_type_success")
    @Enumerated(EnumType.STRING)
    private MailApiType mailApiTypeSuccess;
    @Column(name = "mail_api_types_used")
    @Type(type = "jsonb")
    private List<MailApiType> mailApiTypesUsed;
    @Column(name = "send_to")
    private Long sendTo;
    @Column(name = "status")
    private EmailLogStatusEnum status;
    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "process_send_id")
    private ProcessSend processSend;
    @ManyToOne(cascade = {
            CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id")
    private Email email;
}
