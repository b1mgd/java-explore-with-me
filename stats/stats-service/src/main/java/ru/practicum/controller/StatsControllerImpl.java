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
        log.info("Сервером получен запрос на сохранение посещения: {}", hitPost);
        return statsService.save(hitPost);
    }

    @Override
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsDto> findAllStats(@RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(name = "uris", required = false) List<String> uris,
                                       @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Сервером получен запрос на предоставление статистики со следующими параметрами. " +
                "start: {}, end: {}, uri: {}, unique: {}", start, end, uris, unique);

        return statsService.findAllStats(start, end, uris, unique);
    }
}
