package org.example.ecommercefashion.repositories;


import org.example.ecommercefashion.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT s FROM Category s WHERE (:name IS NULL OR s.name LIKE %:name%) " +
            "AND (:lever IS NULL OR s.lever = :lever)")
    Page<Category> filterCategories(@Param("name") String name,
                                    @Param("lever") Integer lever,
                                    Pageable pageable);

}
