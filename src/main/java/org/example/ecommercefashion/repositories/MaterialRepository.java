package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material,Long> {

    @Query("SELECT m FROM Material m WHERE (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Material> getMaterialPage(@Param("name") String name, Pageable pageable);

    Boolean existsByNameIgnoreCase(String name);
}
