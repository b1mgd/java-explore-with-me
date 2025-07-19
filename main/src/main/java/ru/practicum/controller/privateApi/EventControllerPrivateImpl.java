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
    public List<EventShortDto> findAllUserEvents(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("[PRIVATE] Получение списка созданных событий. userId: {}, from: {}, size: {}", userId, from, size);
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
    public EventDto saveEvent(@PathVariable Long userId,
                              @RequestBody EventUserPost eventPost) {
        log.info("[PRIVATE] Добавление нового события. userId: {}, eventPost: {}", userId, eventPost);
        return eventService.saveEvent(userId, eventPost);
    }

    @Override
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto findESavedEventById(@PathVariable Long userId,
                                        @PathVariable Long eventId) {
        log.info("[PRIVATE] Получение созданного события. userId: {}, eventId: {}", userId, eventId);
        return eventService.findSavedEventById(userId, eventId);
    }

    @Override
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto updateSavedEvent(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @RequestBody EventUserPatch eventPatch) {
        log.info("[PRIVATE] Изменение созданного события. eventId: {}, userId: {}, eventPatch: {}",
                eventId, userId, eventPatch);
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
    public List<ParticipationRequestDto> findAllOtherUsersParticipationRequests(@PathVariable Long userId,
                                                                                @PathVariable Long eventId) {
        log.info("[PRIVATE] Получение списка запросов на участие в созданном событии. userId: {}, eventId: {}",
                userId, eventId);
        return eventService.findAllOtherUsersParticipationRequests(userId, eventId);
    }

    @Override
    @PatchMapping("{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventStatusUpdateDto reviewAllEventParticipationRequests(@PathVariable Long userId,
                                                                    @PathVariable Long eventId,
                                                                    @RequestBody EventStatusUpdateRequest eventStatusUpdateRequest) {
        log.info("[PRIVATE] Изменение статуса запросов на участие в созданном событии. userId: {}, eventId: {}, " +
                "eventStatusUpdateRequest: {}", userId, eventId, eventStatusUpdateRequest);
        return eventService.reviewAllEventParticipationRequests(
                EventParamParticipationStatus.builder()
                        .userId(userId)
                        .eventId(eventId)
                        .eventStatusUpdateRequest(eventStatusUpdateRequest)
                        .build()
        );
    }
}
