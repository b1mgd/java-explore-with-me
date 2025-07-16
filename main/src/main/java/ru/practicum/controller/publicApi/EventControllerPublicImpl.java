package ru.practicum.controller.publicApi;

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
import ru.practicum.service.StatClient;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerPublicImpl implements EventControllerPublic {

    private final EventServicePublic eventService;
    private final StatClient statClient;

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
            @RequestParam(defaultValue = "10") Integer size
    ) {

        log.info("Публичный запрос от клиента на получение списка событий, соответствующих параметрам. " +
                "text: {}, categories: {}, paid: {}, rangeStart: {}, rangeEnd: {}, onlyAvailable: {}, " +
                "sort: {}, from: {}, size: {}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        List<EventShortDto> events = eventService.findAllEvents(
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
                        .build()
        );

//        statClient.hit(new HitPost(
//                app,
//                uri,
//                ip
//        ));

        return events;
    }

    @Override
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto findByEventId(@PathVariable Long eventId) {
        log.info("Публичный запрос от клиента на получение информации об опубликованном событии с eventId: {}", eventId);
        EventDto eventDto = eventService.findByEventId(eventId);

        //        statClient.hit(new HitPost(
//                app,
//                uri,
//                ip
//        ));

        return eventDto;
    }
}
