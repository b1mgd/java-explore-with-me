package ru.practicum.controller.publicApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.RatingEventSummary;
import ru.practicum.service.RatingServicePublic;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class RatingControllerPublicImpl implements RatingControllerPublic {

    private final RatingServicePublic ratingService;

    @Override
    @GetMapping("{eventId}/rating")
    @ResponseStatus(HttpStatus.OK)
    public RatingEventSummary findEventRating(@PathVariable Long eventId) {
        log.info("[PUBLIC] получение рейтинга события с eventId: {}", eventId);
        return ratingService.findEventRating(eventId);
    }

    @Override
    @GetMapping("/rating/top")
    public Page<RatingEventSummary> findAllEventRatings(@PageableDefault Pageable pageable) {
        log.info("[PUBLIC] Получение страницы с рейтингом событий. pageable: {}", pageable);
        return ratingService.findAllEventRatings(pageable);
    }
}
