package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, Long> {
}
