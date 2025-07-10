package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.model.dto.ViewStats;

import java.time.LocalDateTime;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query(
            value = """
            SELECT app, uri, COUNT(uri) 
            FROM hits
            GROUP BY app
            WHERE timestamp IN 
                    """,
            nativeQuery = true)
    ViewStats findAllStats(LocalDateTime start, LocalDateTime end, String[] uri, Boolean unique);
}
