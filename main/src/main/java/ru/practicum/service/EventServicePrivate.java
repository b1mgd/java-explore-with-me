package ru.practicum.service;

import ru.practicum.model.dto.*;
import ru.practicum.model.dto.params.EventParamFindAllUserEvents;
import ru.practicum.model.dto.params.EventParamParticipationStatus;
import ru.practicum.model.dto.params.EventParamUserPatch;

import java.util.List;

public interface EventServicePrivate {

    List<EventShortDto> findAllUserEvents(EventParamFindAllUserEvents param);

    EventDto saveEvent(long userId, EventUserPost eventPost);

    EventDto findSavedEventById(long userId, long eventId);

    EventDto updateSavedEvent(EventParamUserPatch param);

    List<ParticipationRequestDto> findAllOtherUsersParticipationRequests(long userId, long eventId);

    EventStatusUpdateDto reviewAllEventParticipationRequests(EventParamParticipationStatus param);
}
