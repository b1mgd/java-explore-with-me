package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.dto.RatingEventSummary;
import ru.practicum.model.entity.Rating;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @EntityGraph(attributePaths = {"user", "event"})
    Optional<Rating> findByUser_IdAndEvent_Id(long userId, long eventId);

    boolean existsByUser_IdAndEvent_Id(long userId, long eventId);

    void deleteByUser_IdAndEvent_Id(long userId, long eventId);

    @Query(value = """
            SELECT new ru.practicum.model.dto.RatingEventSummary(
                r.event.id,
                SUM(CASE WHEN r.isLike = true THEN 1 ELSE 0 END),
                SUM(CASE WHEN r.isLike = false THEN 1 ELSE 0 END)
            )
            FROM Rating r
            WHERE r.event.id = :eventId
            GROUP BY r.event.id
            """)
    RatingEventSummary findEventRating(@Param("eventId") long eventId);


    @Query(value = """
            SELECT new ru.practicum.model.dto.RatingEventSummary(
                r.event.id,
                SUM(CASE WHEN r.isLike = true THEN 1 ELSE 0 END),
                SUM(CASE WHEN r.isLike = false THEN 1 ELSE 0 END)
            )
            FROM Rating r
            GROUP BY r.event.id
            ORDER BY
                SUM(CASE WHEN r.isLike = true THEN 1 ELSE 0 END) -
                SUM(CASE WHEN r.isLike = false THEN 1 ELSE 0 END) DESC
            """,
            countQuery = """
                        SELECT COUNT(DISTINCT r.event.id)
                        FROM Rating r
                    """)
    Page<RatingEventSummary> findAllEventRatings(Pageable pageable);
}
