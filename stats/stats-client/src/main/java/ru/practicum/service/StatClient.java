package ru.practicum.service;

import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;
import ru.practicum.model.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatClient {

    HitDto save(HitPost hitPost);

    List<StatsDto> findAllStats(LocalDateTime start, LocalDateTime end, List<String> uri, Boolean unique);
}
