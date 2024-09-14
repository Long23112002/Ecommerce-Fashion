package org.example.ecommercefashion.repositories;


import org.example.ecommercefashion.dtos.filter.CategoryParam;
import org.example.ecommercefashion.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT s FROM Category s WHERE " +
            "  (:#{#param.name} IS NULL OR s.name LIKE %:#{#param.name}%) "+
            "AND (:#{#param.createBy} IS NULL OR s.createBy = :#{#param.createBy})" )
    Page<Category> filterCategories(CategoryParam param,
                                    Pageable pageable);
}
