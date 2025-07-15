package ru.practicum.controller.adminApi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.model.dto.EventAdminPatch;
import ru.practicum.model.dto.EventDto;
import ru.practicum.model.entity.utility.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventControllerAdmin {

    List<EventDto> findAllEvents(List<Long> users,
                                 List<State> states,
                                 List<Long> categories,
                                 LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd,
                                 @PositiveOrZero Integer from,
                                 @Positive Integer size);

    EventDto updateEventAdmin(@Positive Long eventId,
                              @Valid EventAdminPatch eventPatch);
}
