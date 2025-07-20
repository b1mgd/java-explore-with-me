package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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
@Validated
@Slf4j
public class StatsControllerImpl implements StatsController {

    private final StatsService statsService;

    @Override
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto save(@RequestBody HitPost hitPost) {
        log.info("StatsService: Сохранение посещения: {}", hitPost);
        return statsService.save(hitPost);
    }

    @Override
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsDto> findAllStats(@RequestParam LocalDateTime start,
                                       @RequestParam LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получение списка StatsDto по параметрам. " +
                "start: {}, end: {}, uri: {}, unique: {}", start, end, uris, unique);

        return statsService.findAllStats(start, end, uris, unique);
    }
}
