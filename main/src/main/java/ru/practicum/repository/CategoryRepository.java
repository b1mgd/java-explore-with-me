package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = """
            SELECT c FROM Category c
            ORDER BY c.id
            LIMIT :size
            OFFSET :from
            """)
    List<Category> findAllCategories(@Param("from") int from,
                                     @Param("size") int size);

    Optional<Category> findByName(String name);
}
