package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {
    Page<Template> findByIsDeleted(Boolean isDeleted, Pageable pageable);
    Page<Template> findAll(Pageable pageable);
    Template findTemplateBySubjectIgnoreCase(String subject);
}
