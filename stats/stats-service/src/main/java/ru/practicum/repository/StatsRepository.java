package ru.practicum.repository;

import ru.practicum.model.Hit;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository {

    Hit save(Hit hit);

    List<Stats> findAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
