package ru.practicum.controller.privateApi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.model.dto.*;

import java.util.List;

public interface EventController {

    List<EventShortDto> findAllUserEvents(@Positive Long userId, @PositiveOrZero Integer from, @Positive Integer size);

    EventDto saveEvent(@Positive Long userId, @Valid EventUserPost eventPost);

    EventDto findESavedEventById(@Positive Long userId, @Positive Long eventId);

    EventDto updateSavedEvent(@Positive Long userId, @Positive Long eventId, @Valid EventUserPatch eventPatch);

    List<ParticipationRequestDto> findAllOtherUsersParticipationRequests(@Positive Long userId, @Positive Long eventId);

    EventStatusUpdateResult reviewAllEventParticipationRequests(@Positive Long userId, @Positive Long eventId, @Valid EventStatusUpdateRequest eventStatusUpdateRequest);
}
