package ru.practicum.service;

import ru.practicum.model.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestServicePrivate {

    List<ParticipationRequestDto> findAllUserRequests(long userId);

    ParticipationRequestDto createRequest(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long requestId);
}
