package ru.practicum.controller.privateApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.*;
import ru.practicum.model.dto.params.EventParamFindAllUserEvents;
import ru.practicum.model.dto.params.EventParamParticipationStatus;
import ru.practicum.model.dto.params.EventParamUserPatch;
import ru.practicum.service.EventServicePrivate;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventControllerPrivateImpl implements EventControllerPrivate {

    private final EventServicePrivate eventService;

    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findAllUserEvents(@PathVariable(name = "userId") Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрос от клиента на получение событий, добавленных текущим пользователем. " +
                "userId: {}, from: {}, size: {}", userId, from, size);
        return eventService.findAllUserEvents(
                EventParamFindAllUserEvents.builder()
                        .userId(userId)
                        .from(from)
                        .size(size)
                        .build()
        );
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto saveEvent(@PathVariable(name = "userId") Long userId,
                              @RequestBody EventUserPost eventPost) {
        log.info("Запрос от клиента на добавление нового события. userId: {}, eventPost: {}", userId, eventPost);
        return eventService.saveEvent(userId, eventPost);
    }

    @Override
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto findESavedEventById(@PathVariable(name = "userId") Long userId,
                                        @PathVariable(name = "eventId") Long eventId) {
        log.info("Запрос от клиента с userId: {} на получение информации о сохраненном им событии c eventId: {}", userId, eventId);
        return eventService.findSavedEventById(userId, eventId);
    }

    @Override
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateSavedEvent(@PathVariable(name = "userId") Long userId,
                                     @PathVariable(name = "eventId") Long eventId,
                                     @RequestBody EventUserPatch eventPatch) {
        log.info("Запрос от клиента на изменение события с eventId: {}, " +
                "добавленного текущим пользователем с userId: {}. eventPatch: {}", eventId, userId, eventPatch);
        return eventService.updateSavedEvent(
                EventParamUserPatch.builder()
                        .userId(userId)
                        .eventId(eventId)
                        .eventPatch(eventPatch)
                        .build()
        );
    }

    @Override
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findAllOtherUsersParticipationRequests(@PathVariable(name = "userId") Long userId,
                                                                                @PathVariable(name = "eventId") Long eventId) {
        log.info("Запрос от клиента на получение информации о запросах на участие в событии, добавленном текущим пользователем. " +
                "userId: {}, eventId: {}", userId, eventId);
        return eventService.findAllOtherUsersParticipationRequests(userId, eventId);
    }

    @Override
    @PatchMapping("{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventStatusUpdateResult reviewAllEventParticipationRequests(@PathVariable(name = "userId") Long userId,
                                                                       @PathVariable(name = "eventId") Long eventId,
                                                                       @RequestBody EventStatusUpdateRequest eventStatusUpdateRequest) {
        log.info("Запрос от клиента на изменение статуса заявок на участие в событии, добавленных текущим пользователем. " +
                "userId: {}, eventId: {}, eventStatusUpdateRequest: {}", userId, eventId, eventStatusUpdateRequest);
        return eventService.reviewAllEventParticipationRequests(
                EventParamParticipationStatus.builder()
                        .userId(userId)
                        .eventId(eventId)
                        .eventStatusUpdateRequest(eventStatusUpdateRequest)
                        .build()
        );
    }
}
