package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Email findFirstBySubjectIgnoreCase(String subject);

    @Query("SELECT e FROM Email e WHERE LOWER(e.subject) = LOWER(:subject) ORDER BY e.id DESC")
    Email findLatestEmailBySubjectIgnoreCase(@Param("subject") String subject);
}
