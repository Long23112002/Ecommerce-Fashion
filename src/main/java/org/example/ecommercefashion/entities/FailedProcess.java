package org.example.ecommercefashion.entities;

@Data
@NoArgsConstructor
public class FailProcess {
    private String process_id;
    private LocalDateTime create_at;
    private int size;
    private int mail_sended;
    private int mail_sended_fail;
}
