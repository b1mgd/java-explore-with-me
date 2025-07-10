package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;
import ru.practicum.model.dto.ViewStats;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class StatsControllerImpl implements StatsController {
    private final StatsService statsService;

    @Override
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto save(@RequestBody HitPost hitPost) {
        log.info("Запрос на сохранение посещения: {}", hitPost);
        return statsService.save(hitPost);
    }

    @Override
    public ViewStats findAllStats(@RequestParam LocalDateTime start,
                                  @RequestParam LocalDateTime end,
                                  @RequestParam String[] uri,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.findAllStats(start, end, uri, unique);
    }
}
