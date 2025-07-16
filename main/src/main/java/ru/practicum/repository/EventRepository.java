package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.entity.Event;
import ru.practicum.model.entity.utility.State;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id = :userId " +
            "ORDER BY e.id " +
            "LIMIT :size " +
            "OFFSET :from ")
    List<Event> findAllEvents(@Param("userId") long userId,
                              @Param("from") int from,
                              @Param("size") int size);

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id = :userId AND e.id = :eventId ")
    Optional<Event> findSavedEventById(@Param("userId") long userId,
                                       @Param("eventId") long eventId);

    @Query("SELECT e.state FROM Event e ")
    State findStateById(long id);
}
