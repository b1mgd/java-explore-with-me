package ru.practicum.controller.publicApi;

import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.model.dto.RatingEventSummary;

public interface RatingControllerPublic {

    RatingEventSummary findEventRating(@Positive Long eventId);

    Page<RatingEventSummary> findAllEventRatings(Pageable pageable);
}
