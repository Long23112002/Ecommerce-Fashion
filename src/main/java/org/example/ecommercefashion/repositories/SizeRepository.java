package org.example.ecommercefashion.repositories;

import org.example.ecommercefashion.entities.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    @Query("SELECT s FROM Size s WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Size> getSizePage(@Param("name") String name, Pageable pageable);

    Boolean existsByName(String name);
}
