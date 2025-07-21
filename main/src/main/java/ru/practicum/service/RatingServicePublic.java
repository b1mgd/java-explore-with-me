package ru.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.model.dto.RatingEventSummary;

public interface RatingServicePublic {

    RatingEventSummary findEventRating(long eventId);

    Page<RatingEventSummary> findAllEventRatings(Pageable pageable);
}
