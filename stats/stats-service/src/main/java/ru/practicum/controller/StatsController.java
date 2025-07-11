package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;
import ru.practicum.model.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsController {

    HitDto save(@Valid HitPost hitPost);

    List<StatsDto> findAllStats(@NotNull LocalDateTime start, @NotNull LocalDateTime end, List<String> uri, Boolean unique);
}
