package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.HitDto;
import ru.practicum.model.dto.HitPost;
import ru.practicum.model.dto.StatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<StatsDto> findAllStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam List<String> uri,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Запрос на предоставление статистики со следующими параметрами.%n" +
                "start: {}%n, end: {}%n, uri: {}%n, unique: {}", start, end, uri, unique);
        return statsService.findAllStats(start, end, uri, unique);
    }
}
