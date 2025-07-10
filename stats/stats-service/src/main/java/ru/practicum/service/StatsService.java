package ru.practicum.service;

import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;
import ru.practicum.model.dto.ViewStats;

import java.time.LocalDateTime;

public interface StatsService {

    HitDto save(HitPost hitPost);

    ViewStats findAllStats(LocalDateTime start, LocalDateTime end, String[] uri, Boolean unique);
}
