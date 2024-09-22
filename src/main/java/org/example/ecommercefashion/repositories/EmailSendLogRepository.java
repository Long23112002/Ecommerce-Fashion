package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.EmailSendLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailSendLogRepository extends JpaRepository<EmailSendLog, Long> {
}
