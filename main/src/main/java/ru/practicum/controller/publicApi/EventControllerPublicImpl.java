package ru.practicum.controller.publicApi;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.EventDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.model.dto.params.EventParamFindAllPublic;
import ru.practicum.model.entity.Category;
import ru.practicum.model.entity.utility.Sort;
import ru.practicum.service.EventServicePublic;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerPublicImpl implements EventControllerPublic {

    private final EventServicePublic eventService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findAllEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Category> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") Sort sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        log.info("[PUBLIC] Получение списка событий по параметрам. text: {}, categories: {}, paid: {}, rangeStart: {}, " +
                        "rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return eventService.findAllEvents(
                EventParamFindAllPublic.builder()
                        .text(text)
                        .categories(categories)
                        .paid(paid)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .onlyAvailable(onlyAvailable)
                        .sort(sort)
                        .from(from)
                        .size(size)
                        .build(),
                request
        );
    }

    @Override
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto findByEventId(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("[PUBLIC] Получение события. eventId: {}", eventId);
        return eventService.findByEventId(eventId, request);
    }
}
