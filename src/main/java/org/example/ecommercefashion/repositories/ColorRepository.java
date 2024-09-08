package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends JpaRepository<Color,Long> {
    @Query("SELECT c FROM Color c WHERE (:name IS NULL OR c.name LIKE %:name%)")
    Page<Color> getColorPage(@Param("name") String name, Pageable pageable);
}
