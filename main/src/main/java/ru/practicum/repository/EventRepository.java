package ru.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.entity.Event;
import ru.practicum.model.entity.utility.State;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @EntityGraph(attributePaths = {"initiator", "category"})
    @Query(value = """
            SELECT e FROM Event e
            WHERE e.initiator.id = :userId
            ORDER BY e.id
            LIMIT :size
            OFFSET :from
            """)
    List<Event> findAllEvents(@Param("userId") long userId,
                              @Param("from") int from,
                              @Param("size") int size);

    @EntityGraph(attributePaths = {"initiator", "category"})
    Optional<Event> findByInitiator_IdAndId(@Param("userId") long userId,
                                            @Param("eventId") long eventId);

    @Query(value = """
            SELECT e.state FROM Event e
            WHERE e.id = :eventId
            """)
    State findStateById(@Param("eventId") Long eventId);

    @EntityGraph(attributePaths = {"initiator", "category"})
    Optional<Event> findByIdAndStateEquals(long eventId, State state);

    @EntityGraph(attributePaths = {"initiator", "category"})
    Optional<Event> findById(Long eventId);

    @EntityGraph(attributePaths = {"initiator", "category"})
    List<Event> findAllByIdIn(List<Long> eventIds);

    boolean existsByCategory_id(long catId);
}
