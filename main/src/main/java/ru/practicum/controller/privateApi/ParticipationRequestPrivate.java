package ru.practicum.controller.privateApi;

import jakarta.validation.constraints.Positive;
import ru.practicum.model.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestPrivate {

    List<ParticipationRequestDto> findAllUserRequests(@Positive Long userId);

    ParticipationRequestDto createRequest(@Positive Long userId, @Positive Long eventId);

    ParticipationRequestDto cancelRequest(@Positive Long userId, @Positive Long requestId);
}
