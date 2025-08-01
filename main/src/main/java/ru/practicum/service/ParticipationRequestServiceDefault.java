package ru.practicum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.dto.ParticipationRequestDto;
import ru.practicum.model.entity.Event;
import ru.practicum.model.entity.ParticipationRequest;
import ru.practicum.model.entity.User;
import ru.practicum.model.entity.utility.State;
import ru.practicum.model.entity.utility.Status;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.ParticipationRequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ParticipationRequestServiceDefault implements ParticipationRequestServicePrivate {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final ParticipationRequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> findAllUserRequests(long userId) {
        userExists(userId);
        List<ParticipationRequest> requests = requestRepository.findAllByRequester_Id(userId);
        log.info("Получены заявки на участие в событиях: {}", requests);

        return requests.stream()
                .map(requestMapper::mapToRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        Event event = getEventById(eventId);
        validateCreateRequest(event, userId);
        Status status = getStatus(event);
        User requester = getUserById(userId);

        ParticipationRequest request = ParticipationRequest.builder()
                .status(status)
                .event(event)
                .requester(requester)
                .build();

        ParticipationRequest savedRequest = requestRepository.save(request);
        updateEvent(status, event);

        log.info("Запрос на участие в событии сохранен: {}", savedRequest);

        return requestMapper.mapToRequestDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        ParticipationRequest request = getRequestById(requestId);
        requestMapper.updateRequest(request, Status.CANCELED);

        ParticipationRequest updatedRequest = requestRepository.save(request);
        log.info("Заявка на участие в событии отменена: {}", updatedRequest);

        return requestMapper.mapToRequestDto(updatedRequest);
    }

    private void userExists(long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new NotFoundException(String.format("Пользователь с userId: %d не был найден", userId));
        }
    }

    private void validateCreateRequest(Event event, long userId) {
        boolean exists = requestRepository.existsByRequester_IdAndEvent_Id(userId, event.getId());

        if (exists) {
            throw new ConflictException(String.format("Повторный запрос от пользователя с userId: %d " +
                    "к событию с eventId: %d не допускается", userId, event.getId()));
        }

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("Инициатор запроса не может быть участником в созданным им событии");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Принять участие можно только в опубликованном событии");
        }

        if (event.getParticipantLimit() != 0 && (event.getParticipantLimit() <= event.getConfirmedRequests())) {
            throw new ConflictException("Достигнуто максимальное количество участников: " + event.getParticipantLimit());
        }
    }

    private void updateEvent(Status status, Event event) {
        if (status == Status.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
    }

    private Status getStatus(Event event) {
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            return Status.CONFIRMED;
        } else {
            return Status.PENDING;
        }
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Мероприятие с eventId: %d не было найдено", eventId)));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с userId: %d не был найден", userId)));

    }

    private ParticipationRequest getRequestById(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Заявка на участие в событии с requestId: %d не была найдена", requestId)));
    }
}
