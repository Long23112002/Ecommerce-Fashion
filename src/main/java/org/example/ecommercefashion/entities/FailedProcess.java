package org.example.ecommercefashion.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class FailedProcess {
    private String process_id;
    private LocalDateTime create_at;
    private int size;
    private int mail_sent;
    private int mail_sent_fail;
}
