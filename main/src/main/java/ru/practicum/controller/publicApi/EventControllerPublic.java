package ru.practicum.controller.publicApi;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.model.dto.EventDto;
import ru.practicum.model.dto.EventShortDto;
import ru.practicum.model.entity.Category;
import ru.practicum.model.entity.utility.Sort;

import java.time.LocalDateTime;
import java.util.List;

public interface EventControllerPublic {

    List<EventShortDto> findAllEvents(
            String text,
            List<Category> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            Sort sort,
            @PositiveOrZero Integer from,
            @Positive Integer size
    );

    EventDto findByEventId(@Positive Long eventId);
}
