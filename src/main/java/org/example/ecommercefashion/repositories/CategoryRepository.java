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
    @Query("SELECT s FROM Category s WHERE s.lever = 1" +
            "AND (:name IS NULL OR s.name LIKE %:name%) "+
            "AND (:createBy IS NULL OR s.createBy = :createBy)")
    Page<Category> filterCategories(@Param("name") String name,
                                    @Param("createBy") Long createBy,
                                    Pageable pageable);
}
