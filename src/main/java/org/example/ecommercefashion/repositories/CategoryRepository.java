package org.example.ecommercefashion.repositories;
import org.example.ecommercefashion.dtos.filter.CategoryParam;
import org.example.ecommercefashion.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT s FROM Category s WHERE s.parentCategory = null " +
            "AND (:#{#param.name} IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :#{#param.name}, '%')))")
    Page<Category> filterCategories(CategoryParam param,
                                    Pageable pageable);
    Boolean existsByName(String name);

    Boolean existsByNameAndIdNot(String name, Long id);
}